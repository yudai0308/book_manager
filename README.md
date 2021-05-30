<div align="center"><img src="https://user-images.githubusercontent.com/34672524/120096560-2adf4680-c167-11eb-86d5-3b1831bfc0ae.png" width="128px"></div>

# 本マ - 本の管理をマークダウンで！

## 概要

Android アプリ「本マ」はシンプルな本の管理アプリです。

感想の記入欄にはマークダウンが利用可能なため、ちょっとしたメモをわかりやすく書き残すのに便利です。

<a href='https://play.google.com/store/apps/details?id=io.github.yudai0308.honma&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'>
<img alt='Google Play で手に入れよう' src='https://play.google.com/intl/ja/badges/static/images/badges/ja_badge_web_generic.png' width="200px"/>
</a>

## スクリーンショット

|  本棚画面  |  検索画面  |  管理画面１  |
| --- | --- | --- |
|  ![screen_shots_01](https://user-images.githubusercontent.com/34672524/120096895-d6d56180-c168-11eb-9bd4-396ccaaf3adc.png)  |  ![screen_shots_02](https://user-images.githubusercontent.com/34672524/120096897-d9d05200-c168-11eb-9c1e-1d669ead0994.png)  |  ![screen_shots_03](https://user-images.githubusercontent.com/34672524/120096899-da68e880-c168-11eb-832b-6ed2b425ee75.png)  |

|  管理画面２  |  管理画面３|  感想記入画面  |
| --- | --- | --- |
|  ![screen_shots_04](https://user-images.githubusercontent.com/34672524/120096901-db017f00-c168-11eb-864d-6441dcbdb176.png)  |  ![screen_shots_05](https://user-images.githubusercontent.com/34672524/120096903-db9a1580-c168-11eb-881d-647cf3621dc2.png)  |  ![screen_shots_06](https://user-images.githubusercontent.com/34672524/120096904-dc32ac00-c168-11eb-8c89-730c3811deee.png)  |

## 機能一覧

### 本棚画面

- 本の一覧表示機能
- フィルター機能
- ソート機能

### 検索画面

- 本の検索機能
  - フリーワード検索
  - タイトル検索
  - 著者名検索
- 本の追加機能

### 管理画面

- 本の表紙変更機能
- 本の評価機能（５段階）
- ステータス管理機能
- 読書開始日、読書終了日管理機能
- 感想入力機能（マークダウン形式 / シンプルテキスト形式）

## 使用技術

### 開発環境

- Android Studio 4.0.2
- Kotlin 1.3.72

### 主なライブラリ

- [Android Jetpack][1] 関連ライブラリ
- [OkHttp][2] 4.6.0
- [Moshi][3] 1.9.2
- [glide][4] 4.11.0
- [Markwon][5] 4.4.0
- [ImagePicker][6] 1.8

### データ解析

- [Firebase Crashlytics][7]
- [Google Analytics for Firebase][8]

### API

- [Google Books APIs][9]

[1]:https://developer.android.com/jetpack?hl=JA
[2]:https://github.com/square/okhttp
[3]:https://github.com/square/moshi
[4]:https://github.com/bumptech/glide
[5]:https://github.com/noties/Markwon
[6]:https://github.com/Dhaval2404/ImagePicker
[7]:https://firebase.google.com/docs/crashlytics?hl=ja
[8]:https://firebase.google.com/docs/analytics?hl=ja
[9]:https://developers.google.com/books

## アーキテクチャ

Android Jetpack をベースとしたアーキテクチャを採用しています。

<img src='https://user-images.githubusercontent.com/34672524/120097121-c2459900-c169-11eb-9bf8-3b5ecfafe051.png' width='500px'>

