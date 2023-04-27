from flask import Flask, request, jsonify
from flask_cors import CORS
import cv2
import mediapipe as mp
import numpy as np
import os
import uuid
import subprocess

app = Flask(__name__)
CORS(app)

@app.route('/detect/pose', methods=['POST'])
def detect_pose():
    # 从POST请求中获取图像文件
    image_file = request.files['image']
    # 生成一个随机的UUID
    random_uuid = str(uuid.uuid4())
    # 处理图像并保存结果
    result = process_image(image_file, random_uuid)
    # 返回JSON响应
    response = jsonify(result)
    # 设置允许的请求来源
    response.headers.add('Access-Control-Allow-Origin', '*')
    return response

def process_image(image_file, random_uuid):
    mp_drawing = mp.solutions.drawing_utils
    mp_pose = mp.solutions.pose

    # 设置输入图片路径、输出关键点文件路径和输出 .blend 文件路径
    # 输入图片
    image_path = "input_pic/1.png"
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
    output_pic_images = "output_pic_images"
    save_folder2 = os.path.join(pic, output_pic_images)
    if not os.path.exists(save_folder2):
        os.makedirs(save_folder2)
    output_image_path = os.path.join(save_folder2, "0.png")

    # 读取输入图像
    image = cv2.imdecode(np.frombuffer(image_file.read(), np.uint8), cv2.IMREAD_UNCHANGED)

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
        
    # 返回处理结果
    result = {
        "uuid": random_uuid,
        "image_path": output_image_path,
        "landmarks_path": output_file,
        "blend_file_path": output_blend_file
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

def run_main():
    # 无需在此处初始化Mediapipe对象
    # 使用Flask应用程序处理请求
    app.run(host='0.0.0.0', port=5000)

