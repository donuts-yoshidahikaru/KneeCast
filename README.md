# KneeCast

KneeCastは現在地や指定した場所の天気予報を表示するAndroidアプリです。

## 機能
- 現在地の天気予報表示
- 住所検索による指定場所の天気予報表示
- 日ごとの天気予報表示
- 時間ごとの詳細な天気データ表示
- お気に入り場所の保存と管理

## 技術スタック
- Android Studio
- Kotlin
- Jetpack Compose（UI）
- Retrofit（ネットワーク通信）
- Google Play Services Location（位置情報取得）
- Yahoo! API（住所検索）
- Meteosource API（天気情報取得）
- Kotlin Coroutines（非同期処理）
- Room（ローカルデータベース）

### APIキーの取得方法
- **Yahoo! API**: [Yahoo! Developer Network](https://developer.yahoo.co.jp/) で開発者アカウントを作成し、アプリケーションIDを取得します。
- **Meteosource API**: [Meteosource](https://www.meteosource.com/) でアカウントを作成し、APIキーを取得します。

## アプリケーションアーキテクチャ

KneeCastはクリーンアーキテクチャとMVVMパターンを採用しています：

```
app/
├── data/           # データ層（リポジトリ、データソース、モデル）
├── di/             # 依存性注入
├── domain/         # ドメイン層（ユースケース、ドメインモデル）
├── network/        # API通信関連
├── location/       # 位置情報取得関連
├── permissions/    # 権限管理
└── ui/             # プレゼンテーション層（画面、ViewModels）
```

### 主要コンポーネント
- **MainActivity**: アプリのメインエントリーポイント
- **AppInitializer**: アプリケーションの初期化処理
- **LocationRepository**: 位置情報取得の責務を持つ
- **WeatherRepository**: 天気情報取得の責務を持つ
- **PermissionHandler**: 権限リクエストと結果処理

## スクリーンショット

### メイン画面：現在地の天気表示
![Screenshot_20250425_101305](https://github.com/user-attachments/assets/72e6124d-3c8c-4b2c-8a4f-855313339b85)

### 住所検索画面：場所を検索して追加
![Screenshot_20250425_101512](https://github.com/user-attachments/assets/7168d9b6-b366-480a-916d-77f63f95f2f2)
