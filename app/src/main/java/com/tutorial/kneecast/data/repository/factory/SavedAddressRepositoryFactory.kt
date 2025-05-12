package com.tutorial.kneecast.data.repository.factory

import android.content.Context
import com.tutorial.kneecast.data.repository.SavedAddressRepository
import com.tutorial.kneecast.data.local.database.SavedAddressDatabase

/**
 * SavedAddressRepositoryのファクトリクラス
 */
object SavedAddressRepositoryFactory {
    /**
     * SavedAddressRepositoryのインスタンスを作成
     * 
     * @param context アプリケーションコンテキスト
     * @return SavedAddressRepository インスタンス
     */
    fun create(context: Context): SavedAddressRepository {
        val database = SavedAddressDatabase.getInstance(context)
        return SavedAddressRepository(database.savedAddressDao(), context)
    }
}