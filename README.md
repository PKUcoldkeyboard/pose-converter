# pose-converter
 A web application for converting videos and images to 3D skeletal poses, providing a forum for users to share and manage their 3D pose files.

---

- `src/ffmpeg.exe`： 必须，需要下载ffmpeg.exe并放在这个位置
- `src/requirments.txt`： 用于安装Python依赖
- `src/main/resources/application.yml`： 配置文件，需要配置的是MINIO的连接信息、MySQL的连接信息以及Sa-Token的密钥
- `src/app.py`: 需要修改 MINIO 的连接信息，以及指定 `blender.exe` 的路径
- 启动：先启动 `app.py` ，再运行 jar 包，然后 `npm run dev` 启动前端。
