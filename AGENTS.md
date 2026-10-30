# 开发原则

- 项目将会长期跟进上游，本仓库的定制化修改必须保证后续容易跟进上游。
- 定制修改应尽量保持局部、可识别、可重放，避免无必要地修改上游核心逻辑。
- 如果修改存在上游合并风险、持久化兼容风险或较大结构风险，应先告知用户，经用户同意后再进行开发。
- 开发过程中，应将相对上游的设计和功能变更记录到 `docs/project.md`。
- 完成定制开发后，应检查并更新 `docs/project.md` 中的变更记录、涉及文件和合并注意事项。

# 提交规范

- 仅当用户明确要求时才能进行提交和推送，禁止自行提交和推送。
- 提交 message 使用以下格式：

```text
feat/fix/build/...: {本次提交总结}

{提交内容分项描述，每项一行}
{提交内容分项描述，每项一行}
```

- `feat/fix/build/...` 表示根据变更类型选择一种前缀，例如 `feat`、`fix`、`build`、`docs`、`refactor`、`test`。
- 提交总结使用简短完整的句子或短语，明确说明本次变更。
- 空行后逐行列出提交内容，每项一行，避免使用大段连续描述。

## Tag Release

### 版本号格式
`v` + 上游版本号 + `-c` + 定制化版本号（从 1 递增）

示例：`v1.7.5-c5`、`v1.7.5-c6`

- **上游版本号**：跟随 `app/build.gradle` 中的 `versionName`（当前 `1.7.5`）
- **定制化版本号**：`c1`、`c2`、`c3`... 依次递增
- **上游升级时**：定制化版本号重置为 `c1`（如上游升至 `1.7.6`，则下个 tag 为 `v1.7.6-c1`）

### Tag Message 规范
必须包含分类变更列表，格式如下：

```
vX.Y.Z-cN — 简短标题

[Category] 分类名
- 变更条目1
- 变更条目2

[Category] 分类名
- 变更条目1
```

分类名使用英文标签（Docs / UI / Features / Fix / Assets），内容使用中文。

- Tag message 必须汇总上一版 Tag 到当前 Tag 之间的全部定制提交，不得只描述最新提交。

### 多行文本处理（Windows 必读）
`git tag -m "多行字符串"` 在 Windows shell 中会**截断多行内容**，仅保留第一行。
**正确做法**：将 message 写入临时文件，用 `-F` 读取：

```powershell
# 错误（Windows 下会截断）
git tag -a v1.7.5-c6 -m "v1.7.5-c6 — 标题
- 第一行
- 第二行"

# 正确：写入临时文件后用 -F 读取
$message = @'
v1.7.5-c6 — 标题

[UI] 分类名
- 第一行
- 第二行
'@
$messageFile = Join-Path $env:TEMP 'materialfiles-tag-msg.txt'
[IO.File]::WriteAllText($messageFile, $message, [Text.UTF8Encoding]::new($false))
git tag -a v1.7.5-c6 -F $messageFile
Remove-Item -LiteralPath $messageFile
```
