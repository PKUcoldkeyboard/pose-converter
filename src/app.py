from flask import Flask, request, jsonify
from flask_cors import CORS
import cv2
import mediapipe as mp
import numpy as np
import os
import uuid
import subprocess
import urllib
import minio

app = Flask(__name__)
app.debug = True
CORS(app)

minio_conf = {
    'endpoint': '127.0.0.1:9000',
    'access_key': 'NtI2o1FUgSdrRFdd',
    'secret_key': 'c6QGip2GR3PftoHfOSQW3XQwTN15gUmr',
    'secure': False
}

def upload_folder(bucket_name, folder_path):
    ret = []
    client = minio.Minio(**minio_conf)
    found = client.bucket_exists(bucket_name)
    
    if not found:
        client.make_bucket(bucket_name)
    
    for root, dirs, files in os.walk(folder_path):
        for file in files:
            file_path = os.path.join(root, file)
            object_name = file_path.replace('\\', '/')
            if object_name.endswith("mp4") or object_name.endswith("png"):
                ret.append(minio_conf['endpoint'] + '/' +  bucket_name + '/' + object_name)
            client.fput_object(bucket_name, object_name, file_path)
            
    return ret

def remove_folder(path):
    if os.path.isdir(path):
        files = os.listdir(path)
        for file in files:
            remove_folder(os.path.join(path, file))
        os.rmdir(path)
    else:
        os.remove(path) 

@app.route('/image/detect/pose', methods=['POST'])
def detect_image_pose():
    # 从POST请求中获取图像文件的URL地址
    data = request.get_json()
    bucket_name = data['bucket_name']
    image_url = data['image_url']
    # 生成一个随机的UUID
    random_uuid = str(uuid.uuid4())
    # 处理图像并保存结果
    result = process_image(bucket_name, image_url, random_uuid)
    # 返回JSON响应
    response = jsonify(result)
    # 设置允许的请求来源
    response.headers.add('Access-Control-Allow-Origin', '*')
    return response

@app.route("/video/detect/pose", methods=['POST'])
def detect_video_pose():
    # 从POST请求中获取图像文件的URL地址
    data = request.get_json()
    bucket_name = data['bucket_name']
    video_url = data['video_url']
    # 生成一个随机的UUID
    random_uuid = str(uuid.uuid4())
    # 处理图像并保存结果
    result = process_video(bucket_name, video_url, random_uuid)
    # 返回JSON响应
    response = jsonify(result)
    # 设置允许的请求来源
    response.headers.add('Access-Control-Allow-Origin', '*')
    return response

def process_image(bucket_name, image_url, random_uuid):
    mp_drawing = mp.solutions.drawing_utils
    mp_pose = mp.solutions.pose

    # 设置输入图片路径、输出关键点文件路径和输出 .blend 文件路径
    # 输入图片
    # image_path = "input_pic/1.png"
    # 设置随机文件
    pic = os.path.join("output_pic", random_uuid)
    # 关键点文件
    output_pic_npy = "output_pic_npy"
    save_folder = os.path.join(pic, output_pic_npy)
    if not os.path.exists(save_folder):
        os.makedirs(save_folder)
    output_file = os.path.join(save_folder, "0.npy")
    # blend 文件
    output_pic_blend = "output_pic_blend"
    save_folder1 = os.path.join(pic, output_pic_blend)
    if not os.path.exists(save_folder1):
        os.makedirs(save_folder1)
    output_blend_file = os.path.join(save_folder1, "0.blend")
    # 图片
    output_pic_images = "output_pic_image"
    save_folder2 = os.path.join(pic, output_pic_images)
    if not os.path.exists(save_folder2):
        os.makedirs(save_folder2)
    output_image_path = os.path.join(save_folder2, "0.png")

    # 读取输入图像
    # image = cv2.imdecode(np.frombuffer(image_file.read(), np.uint8), cv2.IMREAD_UNCHANGED)
    
    req = urllib.request.urlopen(image_url)
    arr = np.asarray(bytearray(req.read()), dtype=np.uint8)
    image = cv2.imdecode(arr, cv2.IMREAD_UNCHANGED)

    if image is not None:
        # 检测姿态关键点
        landmarks = get_landmarks(image)
        if landmarks is not None:
            # 保存关键点到文件
            save_landmarks_to_file(landmarks, output_file)
            # 使用 Blender 脚本创建姿态骨架
            run_blender_script("create_skeleton_pic.py", output_file, output_blend_file)
        else:
            print("未检测到姿态关键点")
    else:
        print("无法读取图像，请检查图像文件")

    # 初始化Mediapipe Pose对象
    with mp_pose.Pose(
            min_detection_confidence=0.5,
            min_tracking_confidence=0.5) as pose:

        # 处理输入图像，提取姿势信息
        results = pose.process(image)

        # 在输出图像中绘制3D骨架
        mp_drawing.draw_landmarks(
            image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS,
            mp_drawing.DrawingSpec(color=(0, 0, 255), thickness=2, circle_radius=2),
            mp_drawing.DrawingSpec(color=(255, 255, 255), thickness=2))

        # 保存输出图像为PNG文件
        cv2.imwrite(output_image_path, image)
    
    # 上传到minio中
    work_dir = 'output_pic/' + random_uuid
    minio_image_path = upload_folder(bucket_name, work_dir)[0]
    
    # 删除创建的临时目录
    remove_folder(work_dir)
        
    # 返回处理结果
    result = {
        "uuid": random_uuid,
        "image_path": minio_image_path,
    }
    return result

def process_video(bucket_name, video_url, random_uuid):
    # 设置随机文件
    vd = os.path.join("output_video", random_uuid)
    # 关键点文件
    output_pic_npy = "output_video_npy"
    output_landmarks_folder = os.path.join(vd, output_pic_npy)
    if not os.path.exists(output_landmarks_folder):
        os.makedirs(output_landmarks_folder)
    # blend 文件
    output_pic_blend = "output_video_blend"
    output_blend_folder = os.path.join(vd, output_pic_blend)
    if not os.path.exists(output_blend_folder):
        os.makedirs(output_blend_folder)
    # 视频
    output_pic_video = "output_video_mp4"
    output_video_mp4_folder = os.path.join(vd, output_pic_video)
    if not os.path.exists(output_video_mp4_folder):
        os.makedirs(output_video_mp4_folder)

    # 创建输出文件夹（如果不存在）
    os.makedirs(output_landmarks_folder, exist_ok=True)
    os.makedirs(output_blend_folder, exist_ok=True)
    os.makedirs(output_video_mp4_folder, exist_ok=True)
    
    # 读取输入视频
    cap = cv2.VideoCapture(video_url)

    # 获取输入视频的宽度、高度和帧率
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    fps = int(cap.get(cv2.CAP_PROP_FPS))

    # 创建输出视频文件
    output_video_path = os.path.join(output_video_mp4_folder, "output_video_with_skeleton.mp4")
    fourcc = cv2.VideoWriter_fourcc(*"mp4v")
    out = cv2.VideoWriter(output_video_path, fourcc, fps, (width, height))
    
    if cap.isOpened():
        frame_idx = 0
        while True:
            ret, frame = cap.read()
            if not ret:
                break

            # 检测姿态关键点
            landmarks = get_landmarks(frame)
            if landmarks is not None:
                # 保存关键点到文件
                output_file = f"{output_landmarks_folder}/landmarks_{frame_idx}.npy"
                save_landmarks_to_file(landmarks, output_file)
                # 使用 Blender 脚本创建姿态骨架
                output_blend_file = f"{output_blend_folder}/skeleton_{frame_idx}.blend"
                run_blender_script("create_skeleton_video.py", output_file, output_blend_file)

                # 在视频帧上绘制 3D 骨架
                frame_with_skeleton = draw_skeleton_on_frame(frame, landmarks)
                out.write(frame_with_skeleton)
            else:
                print(f"未检测到姿态关键点 - Frame {frame_idx}")
                break  # 当没有检测到关键点时，跳出循环

            frame_idx += 1
    else:
        print("无法读取视频，请检查视频路径")

    cap.release()
    out.release()
    
    # 上传到minio中
    work_dir = 'output_video/' + random_uuid
    minio_video_path = upload_folder(bucket_name, work_dir)[0]
    
    # 删除创建的临时目录
    remove_folder(work_dir)
        
    # 返回处理结果
    result = {
        "uuid": random_uuid,
        "video_path": minio_video_path,
    }
    return result
    

def get_landmarks(image):
    # 初始化 mediapipe 的姿态解析模块
    mp_pose = mp.solutions.pose
    pose = mp_pose.Pose()
    # 使用 mediapipe 处理输入图像
    results = pose.process(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
    # 返回检测到的姿态关键点
    return results.pose_landmarks

def save_landmarks_to_file(landmarks, output_file):
    # 将姿态关键点保存到 NumPy 数组中
    landmarks_np = np.array([[landmark.x, landmark.y, landmark.z] for landmark in landmarks.landmark])
    # 保存 NumPy 数组到文件中
    np.save(output_file, landmarks_np)


def run_blender_script(script_path, output_file, output_blend_file):
    # 指定 Blender 可执行文件的路径
    blender_executable = r"C:\Users\cuterwrite\Downloads\blender-2.93.17-windows-x64\blender-2.93.17-windows-x64\blender.exe"
    # 构造用于运行 Blender 脚本的命令
    command = [
        blender_executable,
        "--background",
        "--python",
        script_path,
        "--",
        output_file,
        output_blend_file
    ]
    # 执行命令
    subprocess.run(command, check=True)

def draw_skeleton_on_frame(frame, landmarks):
    # 初始化 mediapipe 的 drawing_utilities 模块
    mp_drawing = mp.solutions.drawing_utils
    # 在当前帧上绘制骨架
    mp_drawing.draw_landmarks(frame, landmarks, mp.solutions.pose.POSE_CONNECTIONS)
    return frame

if __name__ == '__main__':
    # 无需在此处初始化Mediapipe对象
    # 使用Flask应用程序处理请求
    app.run(host='0.0.0.0', port=5000)

