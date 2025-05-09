package com.tutorial.kneecast.ui.components.integrated

import android.location.Location
import com.tutorial.kneecast.data.model.Feature
import com.tutorial.kneecast.data.model.Geometry

/**
 * 現在地をFeatureオブジェクトとして表現するユーティリティクラス
 */
object CurrentLocationFeature {
    /**
     * 位置情報から現在地を表すFeatureオブジェクトを生成
     * 
     * @param location 位置情報
     * @return 現在地を表すFeatureオブジェクト
     */
    fun fromLocation(location: Location): Feature {
        val coordinates = "${location.longitude},${location.latitude}"
        
        return Feature(
            name = "現在地",
            geometry = Geometry(coordinates = coordinates)
        )
    }
    
    /**
     * デフォルト（位置情報なし）の現在地Featureオブジェクトを生成
     * 
     * @return 現在地を表すFeatureオブジェクト
     */
    fun createDefault(): Feature {
        // デフォルト値として東京駅の位置情報を使用
        return Feature(
            name = "現在地",
            geometry = Geometry(coordinates = "139.7673068,35.6809591")
        )
    }
    
    /**
     * 指定されたFeatureが現在地を表すものかどうかを判定
     * 
     * @param feature 判定対象のFeature
     * @return 現在地を表すFeatureの場合はtrue
     */
    fun isCurrentLocation(feature: Feature): Boolean {
        return feature.name == "現在地"
    }
} 