# 项目定制说明

本仓库 fork 自 [upstream](https://github.com/zhanghai/MaterialFiles)。本文件记录本仓库相对上游的设计、功能差异和本地构建方式，主要用于后续同步上游时快速识别并保留定制内容。

## 相对上游的设计和功能变更

### 文件列表界面

以下变更仅作用于文件列表模式，网格模式继续使用上游布局和行为：

- 列表行高度由内容决定，内容上下各保留 `10dp`。
- 列表图标本体为 `28 × 28dp`，外层仍保留可点击区域。
- 列表缩略图使用 `4dp` 圆角。
- 主标题：
  - 最多显示 3 行，超出后省略。
  - 字号为 `15sp`。
  - 默认颜色 `#3D3D3D`，夜间模式颜色 `#DEDEDE`。
- 副标题：
  - 字号为 `12sp`。
  - 默认颜色 `#727272`，夜间模式颜色 `#AEAEAE`。
  - 文件副标题左侧显示文件大小，右侧显示修改时间。
- 主副标题间距为 `4dp`。

相关文件：

- `app/src/main/res/layout/file_item_list.xml`
- `app/src/main/res/values/dimens.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values-night/colors.xml`
- `app/src/main/java/me/zhanghai/android/files/filelist/FileListAdapter.kt`

### 文件夹副标题

- 本地文件夹显示直接子项统计，例如 `2 文件夹  4 文件`。
- 目录数和文件数之间的固定间距为 `8dp`。
- 非本地文件夹固定显示 `文件夹`，避免 FTP、WebDAV、SFTP、SMB 等目录列表请求影响列表性能。
- 当前“显示隐藏文件”设置关闭时，隐藏项目不计入统计；开启后一并统计。
- 统计通过 `DirectoryContents.kt` 在后台执行，并按路径、修改时间和隐藏文件设置缓存。

相关文件：

- `app/src/main/java/me/zhanghai/android/files/filelist/DirectoryContents.kt`
- `app/src/main/java/me/zhanghai/android/files/filelist/FileListAdapter.kt`

### 文件项菜单和选择逻辑

- 列表模式移除文件项右侧三点按钮，保留网格模式原有三点按钮。
- 列表长按恢复为上游原版选择逻辑。
- 仅选中一项时：
  - `剪切`、`复制`、`删除` 保持在选择工具栏固定位置。
  - 右上角三点菜单合并原文件项菜单与原选择菜单，并按动作 ID 去重。
  - 展开顺序为：`打开方式`、`重命名`、`解压`、`压缩`、`分享`、`复制路径`、`添加书签`、`创建快捷方式`、`属性`、`全选`。
- 选中多项时继续使用上游原多选菜单。

相关文件：

- `app/src/main/res/menu/file_list_select_single.xml`
- `app/src/main/java/me/zhanghai/android/files/filelist/FileListFragment.kt`
- `app/src/main/java/me/zhanghai/android/files/filelist/FileListAdapter.kt`

### 上游合并建议

- 尽量把定制逻辑集中在 `FileViewType.LIST` 分支和 `filelist` 相关资源中，不要修改网格模式。
- 合并上游时优先保留上游的数据加载、文件操作和权限逻辑，再套用本仓库的列表视觉和单选菜单差异。
- 新增或调整定制功能后，应在本章节补充变更原因、涉及文件和潜在合并风险。
- 枚举值、设置键和已有资源名如果已被用户数据持久化，不应随意重排或重命名。

## 本地构建方式

### 环境要求

- JDK 21
- Android SDK Platform `37.2`
- Android Build Tools `37.0.0`
- Android NDK `30.0.16248370`
- CMake
- Gradle Wrapper `9.6.0`

Android Gradle Plugin 当前为 `9.4.1`，Kotlin 为 `2.4.20`。具体版本以 `build.gradle`、`app/build.gradle` 和 `gradle/wrapper/gradle-wrapper.properties` 为准。

### 构建调试版

PowerShell：

```powershell
.\gradlew.bat assembleDebug
```

macOS/Linux：

```bash
./gradlew assembleDebug
```

输出 APK：

```text
app/build/outputs/apk/debug/app-debug.apk
```

安装到已连接设备：

```powershell
.\gradlew.bat installDebug
```

或使用 adb：

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### 与 GitHub Actions 一致的检查

```powershell
.\gradlew.bat assembleDebug lintVitalRelease
```

其中：

- `assembleDebug` 生成调试 APK。
- `lintVitalRelease` 执行 Release 变体的关键 Lint 检查。

当前 `.github/workflows/android.yml` 在 push 时执行相同构建，并上传 `app-debug.apk`，Artifact 保留 10 天。

### Release 构建

Release 使用 `signing.gradle` 读取签名配置。可以创建 `signing.properties`，或提供以下环境变量：

```text
STORE_FILE
STORE_PASSWORD
KEY_ALIAS
KEY_PASSWORD
```

随后执行：

```powershell
.\gradlew.bat assembleRelease
```

正式发布前应确认 `applicationId`、签名证书和版本号符合发布目标。

### GitHub Actions Release

推送任意 Git Tag 时，在普通 CI 检查通过后执行：

```text
assembleRelease -> GitHub Release -> app-release.apk
```

Release 签名使用以下 Actions Secrets：

```text
ANDROID_KEYSTORE_BASE64
ANDROID_KEYSTORE_PASSWORD
ANDROID_KEY_ALIAS
ANDROID_KEY_PASSWORD
```

本地 keystore 不要提交到仓库。可以在本机将 `D:\projectsAlpha\haiyiya.jks` 转为 Base64 后，只把结果粘贴到 `ANDROID_KEYSTORE_BASE64` Secret：

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("D:\projectsAlpha\haiyiya.jks"))
```

其余三个 Secret 分别保存 keystore 密码、key alias 和 key 密码。建议将这些 Secrets 放入受保护的 GitHub Environment，并启用人工审批。Release job 只申请 `contents: write` 权限，构建完成后删除临时 keystore，不上传签名文件。
