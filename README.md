# ServerLink
Paper 原生 Transfer 跨服跳转插件，**无需代理、无需 Bungee/Velocity**，客户端直连跨服，轻量稳定。

## 兼容版本
支持 Paper / Purpur / Leaves / Folia
`26.1.x / 26.2 / 26.3`

运行环境：Java自服务端版本决定 

不支持：Spigot、1.20.4 及以下旧版本

## 前置要求
所有目标服务器必须开启：
`accepts-transfers=true`

## 功能介绍
- 原版协议跨服跳转，零代理延迟
- 完整七国语言切换：zh_CN、zh_TW、en_US、ja_JP、ru_RU、fr_FR、de_DE
- 支持热重载配置、无需重启服务端
- 可在线增删跨服服务器别名
- 管理员强制转移玩家
- 全部指令支持 Tab 补全

## 全部指令
/serverlink help                          查看插件帮助

/serverlink transfer <服务器名>           自身跨服跳转

/serverlink optransfer < 玩家 > < 服务器名 >  强制转移指定玩家

/serverlink list                          查看所有可跳转服务器

/serverlink server add < 名称 > <IP> <端口> 新增跨服服务器

/serverlink server remove < 名称 >          删除跨服服务器

/serverlink config                        查看配置说明

/serverlink config config.language < 语言 ID> 在线切换插件语言

/serverlink reload                        重载全部配置与语言

/server                                   查看服务器列表

/server <服务器名>                        快捷跨服跳转

## 权限节点
serverlink.use        基础使用权限（帮助、列表、查看配置）

serverlink.transfer   玩家跨服跳转权限

serverlink.reload     重载配置、切换语言权限

serverlink.optransfer 管理员强制转移玩家权限

serverlink.server     增删服务器列表权限

## 许可协议
MIT License
Copyright (c) 2026 Fmui

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
