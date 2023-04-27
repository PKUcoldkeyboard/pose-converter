import bpy
import numpy as np
import sys
import argparse
import mediapipe as mp

# 定义姿态关键点之间的连接关系
POSE_CONNECTIONS = [
    (0, 1), (1, 2), (2, 3), (3, 7), (0, 4), (4, 5), (5, 6), (6, 8), (10, 9),
    (12, 14), (14, 16), (16, 18), (16, 20), (16, 22), (18, 20),
    (11, 13), (13, 15), (15, 17), (15, 19), (15, 21), (17, 19),
    (11, 12), (11, 23), (23, 24), (12, 24),
    (23, 25), (25, 27), (27, 29), (27, 31), (29, 31),
    (24, 26), (26, 28), (28, 30), (28, 32), (30, 32)
]

# 创建骨架并将关键点设置为骨头的位置
def create_skeleton(landmarks_np):
    armature = bpy.data.armatures.new("Skeleton")
    obj = bpy.data.objects.new("Skeleton", armature)
    bpy.context.collection.objects.link(obj)
    bpy.context.view_layer.objects.active = obj
    obj.select_set(True)

    bpy.ops.object.mode_set(mode="EDIT")

    scale_factor = 5

    bones = []

    # 遍历姿态关键点，创建骨头
    for index, landmark in enumerate(landmarks_np):
        x, y, z = landmark[1] * scale_factor, landmark[0] * scale_factor, -landmark[2] * scale_factor
        bone = armature.edit_bones.new(f"landmark_{index}")
        bone.head = np.array([x, y, z])
        bone.tail = np.array([x, y, z]) + np.array([0, 0, 0.01])
        bones.append(bone)

    # 创建骨头之间的连接关系
    for connection in POSE_CONNECTIONS:
        parent, child = connection
        bones[child].parent = bones[parent]
        bones[child].use_connect = True

    bpy.ops.object.mode_set(mode="OBJECT")

    return obj

# 在视频帧上绘制 3D 骨架
def draw_skeleton_on_frame(frame, landmarks):
    # 初始化 mediapipe 的 drawing_utilities 模块
    mp_drawing = mp.solutions.drawing_utils
    # 在当前帧上绘制骨架
    mp_drawing.draw_landmarks(frame, landmarks, mp.solutions.pose.POSE_CONNECTIONS)
    return frame

# 主函数，用于加载关键点数据并创建骨架
def run_main():
    argv = sys.argv
    if "--" not in argv:
        argv = []
    else:
        argv = argv[argv.index("--") + 1:]

    parser = argparse.ArgumentParser()
    parser.add_argument("input_landmarks", type=str, help="Path to the input landmarks file")
    parser.add_argument("output_blend", type=str, help="Path to the output .blend file")
    args = parser.parse_args(argv)

    input_landmarks = args.input_landmarks
    output_blend = args.output_blend

    # 加载关键点数据
    landmarks_np = np.load(input_landmarks)

    # 删除场景中所有对象
    bpy.ops.object.select_all(action="SELECT")
    bpy.ops.object.delete()

    # 使用关键点数据创建骨架
    create_skeleton(landmarks_np)

    # 保存生成的骨架到 .blend 文件
    bpy.ops.wm.save_as_mainfile(filepath=output_blend)

if __name__ == "__main__":
    run_main()

