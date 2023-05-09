import os

# 视频转码
input_video_path = 'output_video\\b057c26d-799a-440f-92f9-c3f7509fb7d3\\output_video_mp4\\output_video_with_skeleton.mp4'
output_video_path_compatible = os.path.join('output_video\\b057c26d-799a-440f-92f9-c3f7509fb7d3\\output_video_mp4\\', "output_video_with_skeleton_compatible.mp4")

# 转码参数
input_args = "-y -i"
output_args = "-c:v libx264 -profile:v high -level 4.0 -pix_fmt yuv420p -preset slow -crf 22 -c:a aac -b:a 128k -movflags +faststart"

cmd = f"ffmpeg.exe {input_args} {input_video_path} {output_args} {output_video_path_compatible}"
os.system(cmd)

# 删除原始视频文件
os.remove(input_video_path)

# 更新输出视频路径
output_video_path = output_video_path_compatible