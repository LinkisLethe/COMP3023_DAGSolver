# DAG 顶点加权最大路径

[English](README.md) | [简体中文](README.zh-CN.md)

[![Build](https://img.shields.io/github/actions/workflow/status/LinkisLethe/COMP3023_DAGSolver/ci.yml?branch=main&style=flat-square&label=build)](https://github.com/LinkisLethe/COMP3023_DAGSolver/actions/workflows/ci.yml)
![Java 8+](https://img.shields.io/badge/Java-8%2B-007396?style=flat-square&logo=openjdk&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

这是一个求解顶点加权有向无环图最大路径的 Java 命令行项目。程序读取邻接矩阵，检查图中是否存在环，并输出指定起点与终点之间的最大权重及对应路径。

当前版本保留原有文本输入格式，将基于矩阵扫描的求解过程改为邻接表、Kahn 拓扑排序和动态规划。顶点权重可以是正数或负数。

## 快速运行

环境要求：Java 8 或更高版本。Maven Wrapper 会在首次运行时下载所需的 Maven 版本。

Windows：

```powershell
.\mvnw.cmd clean verify
java -jar target\dag-maximum-weight-path-1.0.0.jar examples\sample.in
Get-Content examples\sample.in_out
```

Linux 或 macOS：

```bash
./mvnw clean verify
java -jar target/dag-maximum-weight-path-1.0.0.jar examples/sample.in
cat examples/sample.in_out
```

样例结果应与 [`examples/sample.expected`](examples/sample.expected) 一致。

## 输入与输出

输入文件依次包含：

1. 顶点数 `n`；
2. 起点和终点编号；
3. `n` 个顶点权重；
4. 仅由 `0` 和 `1` 组成的 `n × n` 邻接矩阵。

顶点从 `0` 开始编号。空格、制表符和换行都可作为分隔符。命令行的第二个参数可指定输出路径；省略时，程序会在输入路径后添加 `_out`。

终点可达时，输出最大总权重和所选路径：

```text
9
0 1 3
```

终点不可达时输出：

```text
0
No path
```

如果输入含环、矩阵格式错误、顶点编号越界、存在多余数据或路径权重溢出，程序会报告错误。

## 算法与复杂度

解析器先把邻接矩阵转换为邻接表。求解器计算各顶点入度，通过 Kahn 算法生成拓扑序，再按该顺序松弛出边，最后利用前驱数组还原路径。

| 阶段 | 时间 | 额外空间 |
|---|---:|---:|
| 解析邻接矩阵 | `O(V²)` | `O(V + E)` |
| 拓扑排序与路径动态规划 | `O(V + E)` | `O(V)` |

由于输入仍使用矩阵，完整解析时间是 `O(V²)`；图存入内存后，求解过程是线性的。多条路径权重相同时，程序按输入顶点顺序稳定选择，但不保证得到字典序最小路径。

## 验证

`mvnw verify` 会运行 8 项 JUnit 检查，覆盖给定样例、起终点相同、终点不可达、等权路径、环检测、混合空白符、非法矩阵值，以及 200 个固定随机种子的 DAG。随机图结果会与穷举路径结果交叉核对。

## 目录结构

```text
examples/   样例输入与预期输出
legacy/     已去除个人信息的课程实现
src/main/   解析器、图模型、求解器与命令行入口
src/test/   单元测试与随机回归测试
```

## 来源说明

项目起源于 COMP3023 Design and Analysis of Algorithms 的算法练习。`legacy/` 保留了已去除个人信息的课程实现；Maven 工程结构、输入校验、邻接表求解器、测试、CI 和文档属于后续维护，具体变化记录在 Git 历史中。

## 许可证

[MIT](LICENSE)
