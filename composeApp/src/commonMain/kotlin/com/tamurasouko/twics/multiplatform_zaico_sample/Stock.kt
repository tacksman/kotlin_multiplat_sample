package com.tamurasouko.twics.multiplatform_zaico_sample

import kotlinx.serialization.Serializable

@Serializable data class Stock(var title: String) {

    protected var id: Long = 0

}

//
//import android.content.ContentUris
//import android.content.ContentValues
//import android.content.Context
//import android.database.Cursor
//import android.net.Uri
//import android.os.Bundle
//import android.text.TextUtils
//
//import com.google.firebase.analytics.FirebaseAnalytics
//import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.tamurasouko.twics.inventorymanager.AccountManager
//import com.tamurasouko.twics.inventorymanager.AccountManager.NoValidAccountException
//import com.tamurasouko.twics.inventorymanager.AccountManager.UnavailableUserException
//import com.tamurasouko.twics.inventorymanager.BuildConfig
//import com.tamurasouko.twics.inventorymanager.InventoryManagerApplication
//import com.tamurasouko.twics.inventorymanager.R
//import com.tamurasouko.twics.inventorymanager.contentprovider.DatabaseContract
//import com.tamurasouko.twics.inventorymanager.contentprovider.InventoryProvider
//import com.tamurasouko.twics.inventorymanager.net.ApiClient
//import com.tamurasouko.twics.inventorymanager.net.ApiClientManager
//import com.tamurasouko.twics.inventorymanager.net.ZaicoHttpException
//import com.tamurasouko.twics.inventorymanager.shared_preference_dao.SyncInfoDao
//import com.tamurasouko.twics.inventorymanager.shared_preference_dao.UiSelectionDao
//import com.tamurasouko.twics.inventorymanager.ui.inventories.InventoryUtilsKt
//import com.tamurasouko.twics.inventorymanager.util.FileConvention
//import com.tamurasouko.twics.inventorymanager.util.MiscUtils
//
//import org.apache.commons.io.FileUtils
//import org.json.JSONArray
//import org.json.JSONException
//import org.json.JSONObject
//
//import androidx.annotation.VisibleForTesting
//import okhttp3.MediaType
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import okhttp3.ResponseBody
//import retrofit2.Call
//import retrofit2.Response
//
//import com.tamurasouko.twics.inventorymanager.AccountManager.AvailableFunction.CreateUnlimitedNumberOfStocks
//import com.tamurasouko.twics.inventorymanager.contentprovider.DatabaseContract.Stock.NOTHING
//import com.tamurasouko.twics.inventorymanager.net.ApiUtilKt.throwIfApiCallFailed
//import com.tamurasouko.twics.inventorymanager.net.ApiUtilKt.toRequestBodyString
//
///**
// * 物品モデル.サーバ側のDBの物品のスキーマについても管理する.
// *
// * @author Noritoshi Miyashita
// */
//data class Stock : InventoryObject, Serializable {
//
//    constructor()
//
//    /**
//     * モバイルのDBの_idカラムの値
//     */
//    var id: Long = 0
//        protected set
//    var mCreateUserName: String? = null
//    var updateUserName: String? = null
//    var userGroup: String? = null
//    var title: String? = null
//
//    // 変更前の数量
//    // transaction_typeがrelativeの場合のみ変更履歴にて利用する
//    var mQuantityBefore: String? = null
//    var mQuantity: Int? = null
//    var mOrderPoint: Int? = null
//    var mLogicalQuantity: Int? = null
//    var mPlannedPurchaseItemsQuantity: Int? = null
//    var mPlannedDeliveriesQuantity: Int? = null
//    private var mCategory: ArrayList<String>? = null
//    var unit: String? = null
//    var place: String? = null
//    var state: String? = null
//    var code: String? = null
//    private var mSyncState: Int? = null
//
//    /**
//     * 写真のファイル名.photoPlaceがServerの場合はurlのパス部分を示し、Localの場合はファイル名を示す.
//     * 物品が削除された場合は写真ファイルも削除する.ただし、mPhotoFileNameの値は削除しない.
//     */
//    var photoFileName: String? = null
//    var etc: String? = null
//    private var mOptionalAttribute: java.util.HashMap<String, OptionalAttribute>? = null
//
//    /**
//     * 前回同期した際のmUpdateAt
//     */
//    private var mUpdatedAtWhenSync: Long? = null
//
//    var mIsReceiveZaicon: Boolean? = null
//    var mIsCheckedOrderPointWarningDisabled: Boolean = false
//
//    /**
//     * 画面表示用の更新時刻
//     */
//    private var mUpdateDate: Long? = null
//
//    /**
//     * 単位自動換算 単位による自動換算を行うかどうか
//     */
//    var isQuantityAutoConversionByUnit: Boolean = false
//
//    /**
//     * 単位自動換算 単位による自動換算を行う際のまとめ単位名称
//     */
//    var quantityAutoConversionByUnitName: String? = null
//        private set
//
//    /**
//     * 単位自動換算 単位による自動換算を行う際のまとめ位のレート
//     */
//    private var mQuantityAutoConversionByUnitFactor: Int? = null
//
//    /**
//     * 発注時適正在庫数
//     */
//    private var mOptimalInventoryLevel: Int? = null
//
//    /**
//     * 商品IDを取得
//     */
//    /**
//     * 商品ID
//     */
//    var inventoryId: Int = 0
//        private set
//
//    /**
//     * 商品マスタIDを取得
//     */
//    /**
//     * 商品マスタID
//     */
//    var inventoryMasterId: Int = 0
//        private set
//
//    /**
//     * Unit Testで使用するAccountManager。Unit Test以外では使用しないこと。
//     */
//    private var accountManagerForTest: AccountManager? = null
//
//    fun setIsReceiveZaicon(isReceiveZaicon: Boolean) {
//        mIsReceiveZaicon = isReceiveZaicon
//    }
//
//    fun setIsOrderPointWarningDisabled(isOrderPointWarningDisabled: Boolean) {
//        mIsCheckedOrderPointWarningDisabled = isOrderPointWarningDisabled
//    }
//
//    /**
//     * URL'inventories/since/:since_id'のレスポンスのうち、sync_timeとsince_idを格納するデータ型。
//     */
//    private class SyncTimeAndSinceId(var syncTime: Long, var sinceId: Int)
//
//    /**
//     * コンストラクタ.物品を新規作成する.
//     *
//     * @param context コンテキスト
//     */
//    private constructor(context: android.content.Context) : super(context) {
//        mSyncState = 0
//        userGroup = AccountManager(context).getUserGroup()
//        mUpdatedAtWhenSync = -1L
//    }
//
//    var quantity: Int?
//        get() = mQuantity
//        set(quantity) {
//            updateQuantityBefore()
//            mQuantity = MiscUtils.parseQuantity(quantity)
//            calcLogicalQuantity()
//        }
//
//    var orderPoint: Int?
//        get() = mOrderPoint
//        set(orderPoint) {
//            mOrderPoint = MiscUtils.parseQuantity(orderPoint)
//        }
//
//    val logicalQuantity: Int?
//        get() = mLogicalQuantity
//
//    val plannedPurchaseItemsQuantity: Int?
//        get() = mPlannedPurchaseItemsQuantity
//
//    val plannedDeliveriesQuantity: Int?
//        get() = mPlannedDeliveriesQuantity
//
//    var category: java.util.ArrayList<String>?
//        get() = mCategory
//        set(categoryList) {
//            if (categoryList == null) {
//                mCategory = null
//            } else {
//                mCategory = java.util.ArrayList<String>(categoryList)
//            }
//        }
//
//    val updateDate: Long
//        get() = (mUpdateDate)!!
//
//    val quantityAutoConversionByUnitFactor: Int?
//        get() = mQuantityAutoConversionByUnitFactor
//
//    var optionalAttribute: java.util.HashMap<String, OptionalAttribute>?
//        get() = mOptionalAttribute
//        set(optionalAttribute) {
//            if (optionalAttribute == null) {
//                mOptionalAttribute = null
//            } else {
//                mOptionalAttribute = java.util.HashMap(optionalAttribute)
//            }
//        }
//
//    private fun updateQuantityBefore() {
//        if (mQuantityBefore != null) return
//        mQuantityBefore = if ((mQuantity == null)) "" else mQuantity.toString()
//    }
//
//    // 予定フリー在庫数を更新する
//    private fun calcLogicalQuantity() {
//        if (mQuantity == null) {
//            mLogicalQuantity = null
//            return
//        }
//
//        val plannedPurchaseItemsQuantity: Int =
//            if (mPlannedPurchaseItemsQuantity == null) Int.ZERO else mPlannedPurchaseItemsQuantity
//        val plannedDeliveriesQuantity: Int =
//            if (mPlannedDeliveriesQuantity == null) Int.ZERO else mPlannedDeliveriesQuantity
//        mLogicalQuantity =
//            mQuantity.add(plannedPurchaseItemsQuantity).subtract(plannedDeliveriesQuantity)
//    }
//
//    /**
//     * EditBoxなどのデフォルト値として数量を表示を取得.
//     *
//     * @return
//     */
//    fun formatQuantityForInput(): String {
//        return MiscUtils.formatQuantityWithoutGrouping(mQuantity)
//    }
//
//    fun formatOrderPointForInput(): String {
//        return MiscUtils.formatQuantityWithoutGrouping(mOrderPoint)
//    }
//
//    /**
//     * TextViewなどの数量の表示における文字列を取得.
//     *
//     * @return
//     */
//    fun formatQuantityForDisplay(): String {
//        return MiscUtils.formatQuantityWithoutGrouping(mQuantity)
//    }
//
//    val name: String?
//        get() = title
//
//    /**
//     * コードにデフォルトの値をセットする.
//     */
//    fun setDefaultCode() {
//        this.code = defaultCode
//    }
//
//    val defaultCode: String
//        /**
//         * コードのデフォルトの値を取得する.
//         *
//         * @return デフォルトのコード.
//         */
//        get() = DEFAULT_CODE_PREFIX + getCommonId()
//
//    /**
//     * カテゴリをすべて削除.
//     */
//    fun clearCategory() {
//        mCategory = null
//    }
//
//    @Throws(java.io.IOException::class)
//    protected fun insertFromServer(context: android.content.Context?): Boolean {
//        return super.insertFromServer(context)
//    }
//
//    @Throws(java.io.IOException::class)
//    protected fun updateFromServer(context: android.content.Context): Boolean {
//        if (mDelFlg && !android.text.TextUtils.isEmpty(photoFileName)) {
//            deletePhotoFile(context)
//        }
//
//        return super.updateFromServer(context)
//    }
//
//    protected val propertyForLocalDb: ContentValues
//        get() {
//            val values: ContentValues = ContentValues()
//            // @formatter:off
//            // idは保存する際に使わない。_idは変更しない。
//            values.put(DatabaseContract.Stock.COLUMN_NAME_COMMON_ID, mCommonId)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_CREATED_AT, mCreatedAt)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_UPDATED_AT, mUpdatedAt)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_UPDATE_DATE, mUpdateDate)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_CREATE_USER_ID, mCreateUserId)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_CREATE_USER_NAME, mCreateUserName)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_UPDATE_USER_ID, mUpdateUserId)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_UPDATE_USER_NAME, updateUserName)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_COMPANY_ID, mCompanyId)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_USER_GROUP, userGroup)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_DEL_FLG, if (mDelFlg)DatabaseContract.Stock.TRUE else DatabaseContract.Stock.FALSE)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_TITLE, title)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_STATE, state)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_PLACE, place)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_QUANTITY, if (mQuantity != null)mQuantity.toString() else null) // Quantityはsqlliteに保存する場合、BigDecimal.toString()で、復元する場合、new BigDecimal()で行う。
//            values.put(DatabaseContract.Stock.COLUMN_NAME_ORDER_POINT, if (mOrderPoint == null)null else mOrderPoint.toString())
//            values.put(DatabaseContract.Stock.COLUMN_NAME_ORDER_POINT_WARNING_DISABLED, if (mIsCheckedOrderPointWarningDisabled)DatabaseContract.Stock.TRUE else DatabaseContract.Stock.FALSE)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_LOGICAL_QUANTITY, if (mLogicalQuantity == null)null else mLogicalQuantity.toString())
//            values.put(DatabaseContract.Stock.COLUMN_NAME_PLANNED_PURCHASE_ITEMS_QUANTITY, if (mPlannedPurchaseItemsQuantity == null)null else mPlannedPurchaseItemsQuantity.toString())
//            values.put(DatabaseContract.Stock.COLUMN_NAME_PLANNED_DELIVERIES_QUANTITY, if (mPlannedDeliveriesQuantity == null)null else mPlannedDeliveriesQuantity.toString())
//            values.put(DatabaseContract.Stock.COLUMN_NAME_CODE, this.code)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_ETC, etc)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_PHOTO_FILE_NAME, photoFileName)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_UNIT, unit)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_CATEGORY, serializedCategory)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_OPTIONAL_ATTRIBUTES, OptionalAttribute.Companion.getSerializedAttribute(mOptionalAttribute))
//            values.put(DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE, mSyncState)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_CHECKED_AT, mCheckedAt)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_UPDATED_AT_WHEN_SYNC, mUpdatedAtWhenSync)
//            if (mIsReceiveZaicon != null) {
//                values.put(DatabaseContract.Stock.COLUMN_NAME_RECEIVE_ZAICON, mIsReceiveZaicon)
//            }
//            values.put(DatabaseContract.Stock.COLUMN_NAME_IS_QUANTITY_AUTO_CONVERSION_BY_UNIT, if (isQuantityAutoConversionByUnit)DatabaseContract.Stock.TRUE else DatabaseContract.Stock.FALSE)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_QUANTITY_AUTO_CONVERSION_BY_UNIT_NAME, if (quantityAutoConversionByUnitName == null)null else quantityAutoConversionByUnitName)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_QUANTITY_AUTO_CONVERSION_BY_UNIT_FACTOR, if (mQuantityAutoConversionByUnitFactor == null)null else mQuantityAutoConversionByUnitFactor.toString())
//            values.put(DatabaseContract.Stock.COLUMN_NAME_OPTIMAL_INVENTORY_LEVEL, if (mOptimalInventoryLevel == null)null else mOptimalInventoryLevel.toString())
//            values.put(DatabaseContract.Stock.COLUMN_NAME_INVENTORY_ID, inventoryId)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_INVENTORY_MASTER_ID, inventoryMasterId)
//
//            // @formatter:on
//            return values
//        }
//
//    val serializedCategory: String?
//        /**
//         * カテゴリをシリアライズする.
//         *
//         * @return カテゴリが一つもないならnull.
//         */
//        get() {
//            if (mCategory == null) {
//                return null
//            }
//
//            return android.text.TextUtils.join(",", mCategory)
//        }
//
//    /**
//     * サーバーからの値でプロパティの値をセットする.keyはサーバーにおける物品のプロパティ名.
//     */
//    @Throws(java.text.ParseException::class)
//    fun setPropertyWithServerData(stockInMap: Map<String?, Any?>): Stock {
//        var `val`: Any?
//        // @formatter:off
//        `val` = stockInMap["id"]
//        if (`val` != null) {
//            mIdInServerDB = `val` as String
//        }
//        `val` = stockInMap["common_id"]
//        if (`val` != null) {
//            mCommonId = `val` as String
//        }
//        `val` = stockInMap["title"]
//        if (`val` != null) {
//            title = `val` as String?
//        }
//        `val` = stockInMap["updated_at"]
//        if (`val` != null) {
//            mUpdatedAt = MiscUtils.parseIso8601ExtendedFormat(`val` as String?)
//        }
//        `val` = stockInMap["update_date"]
//        if (`val` != null) {
//            mUpdateDate = MiscUtils.parseIso8601ExtendedFormat(`val` as String?)
//        }
//        `val` = stockInMap["quantity"]
//        if (`val` != null) {
//            mQuantity = MiscUtils.parseQuantityFromRails(`val` as String?)
//        }
//        `val` = stockInMap["quantity_management"]
//        if (`val` != null) {
//            if ((`val` as Map<String?, String?>)["order_point_quantity"] != null) {
//                mOrderPoint = MiscUtils.parseQuantityFromRails(`val`["order_point_quantity"])
//            }
//            if (`val`["warning_disabled"] != null) {
//                mIsCheckedOrderPointWarningDisabled = (`val` as Map<String?, String>)["warning_disabled"] == "1"
//            }
//            if (`val`["optimal_inventory_level"] != null) {
//                mOptimalInventoryLevel = MiscUtils.parseQuantityFromRails(`val`["optimal_inventory_level"])
//            }
//        }
//        `val` = stockInMap["logical_quantity"]
//        if (`val` != null) {
//            mLogicalQuantity = MiscUtils.parseQuantityFromRails(`val` as String?)
//        }
//        `val` = stockInMap["planned_purchase_items_quantity"]
//        if (`val` != null) {
//            mPlannedPurchaseItemsQuantity = MiscUtils.parseQuantityFromRails(`val` as String?)
//        }
//        `val` = stockInMap["planned_deliveries_quantity"]
//        if (`val` != null) {
//            mPlannedDeliveriesQuantity = MiscUtils.parseQuantityFromRails(`val` as String?)
//        }
//        `val` = stockInMap["del_flg"]
//        if (`val` != null) {
//            mDelFlg = `val` == "1"
//        }
//        `val` = stockInMap["state"]
//        if (`val` != null) {
//            state = `val` as String?
//        }
//        `val` = stockInMap["place"]
//        if (`val` != null) {
//            place = `val` as String?
//        }
//        `val` = stockInMap["code"]
//        if (`val` != null) {
//            this.code = `val` as String?
//        }
//        `val` = stockInMap["etc"]
//        if (`val` != null) {
//            etc = `val` as String?
//        }
//        `val` = stockInMap["create_user_id"]
//        if (`val` != null) {
//            mCreateUserId = `val` as String
//        }
//        `val` = stockInMap["update_user_id"]
//        if (`val` != null) {
//            mUpdateUserId = `val` as String
//        }
//        `val` = stockInMap["created_at"]
//        if (`val` != null) {
//            mCreatedAt = MiscUtils.parseIso8601ExtendedFormat(`val` as String?)
//        }
//        `val` = stockInMap["unit"]
//        if (`val` != null) {
//            unit = `val` as String?
//        }
//        `val` = stockInMap["category"]
//        if (`val` != null) {
//            mCategory = parseCategory(`val` as String)
//        }
//        `val` = stockInMap["create_user_name"]
//        if (`val` != null) {
//            mCreateUserName = `val` as String?
//        }
//        `val` = stockInMap["update_user_name"]
//        if (`val` != null) {
//            updateUserName = `val` as String?
//        }
//        `val` = stockInMap["company_id"]
//        if (`val` != null) {
//            mCompanyId = (`val` as String).toLong()
//        }
//        `val` = stockInMap["optional_attributes"]
//        if (`val` != null) {
//            mOptionalAttribute = OptionalAttribute.Companion.parseAttribute(`val` as String?)
//        }
//        `val` = stockInMap["user_group"]
//        if (`val` != null) {
//            userGroup = `val` as String?
//        }
//        `val` = stockInMap["item_image"]
//        if (`val` != null && (`val` as Map<String?, String?>)["url"] != null) {
//            photoFileName = `val`["url"]
//        }
//        `val` = stockInMap["stocktake"]
//        if (`val` != null && (`val` as Map<String?, String?>)["checked_at"] != null) {
//            mCheckedAt = MiscUtils.parseIso8601ExtendedFormat(`val`["checked_at"])
//        }
//        `val` = stockInMap["is_quantity_auto_conversion_by_unit"]
//        if (`val` != null) {
//            isQuantityAutoConversionByUnit = `val` == "1"
//        }
//        `val` = stockInMap["quantity_auto_conversion_by_unit_name"]
//        if (`val` != null) {
//            quantityAutoConversionByUnitName = `val` as String?
//        }
//        `val` = stockInMap["quantity_auto_conversion_by_unit_factor"]
//        if (`val` != null) {
//            mQuantityAutoConversionByUnitFactor = MiscUtils.parseQuantityFromRails(`val` as String?)
//        }
//        `val` = stockInMap["id"]
//        if (`val` != null) {
//            inventoryId = (`val` as String).toInt()
//        }
//        `val` = stockInMap["inventory_master_id"]
//        if (`val` != null) {
//            inventoryMasterId = (`val` as String).toInt()
//        }
//
//        // @formatter:on
//        mUpdatedAtWhenSync = mUpdatedAt
//
//        mSyncState = NOTHING
//
//        return this
//    }
//
//    /**
//     * カテゴリをデシリアライズする.
//     *
//     * @param val
//     * @return 空文字ならnull.
//     */
//    private fun parseCategory(`val`: String): java.util.ArrayList<String>? {
//        if (android.text.TextUtils.isEmpty(`val`)) {
//            return null
//        }
//
//        return java.util.ArrayList<String>(
//            java.util.Arrays.asList<String>(
//                *`val`.split(",".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()))
//    }
//
//    /**
//     * マップで各フィールドの値をセット.
//     *
//     * @param columnMap
//     */
//    fun setPropertyWithMap(columnMap: Map<String?, String?>) {
//        setPropertyWithLocalDb(columnMap)
//    }
//
//    /**
//     * ローカルDBの値をインスタンスにセット
//     */
//    protected fun setPropertyWithLocalDb(columnMap: Map<String?, String?>) {
//        var `val`: String?
//        // @formatter:off
//        `val` = columnMap[DatabaseContract.Stock._ID]
//        if (`val` != null) {
//            id = `val`.toInt().toLong()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_COMMON_ID]
//        if (`val` != null) {
//            mCommonId = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_TITLE]
//        if (`val` != null) {
//            title = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_UPDATED_AT]
//        if (`val` != null) {
//            mUpdatedAt = `val`.toLong()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_UPDATE_DATE]
//        if (`val` != null) {
//            mUpdateDate = `val`.toLong()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_CATEGORY]
//        if (`val` != null) {
//            mCategory = parseCategory(`val`)
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_QUANTITY]
//        if (`val` != null) {
//            mQuantity = Int(`val`) // Quantityはsqlliteに保存する場合、BigDecimal.toString()で、復元する場合、new BigDecimal()で行う。
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_ORDER_POINT]
//        if (`val` != null) {
//            mOrderPoint = Int(`val`) // sqlliteに保存する場合、BigDecimal.toString()で、復元する場合、new BigDecimal()で行う。
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_ORDER_POINT_WARNING_DISABLED]
//        if (`val` != null) {
//            mIsCheckedOrderPointWarningDisabled = DatabaseContract.Stock.TRUE.equals(`val`)
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_LOGICAL_QUANTITY]
//        if (`val` != null) {
//            mLogicalQuantity = Int(`val`) // sqlliteに保存する場合、BigDecimal.toString()で、復元する場合、new BigDecimal()で行う。
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_PLANNED_PURCHASE_ITEMS_QUANTITY]
//        if (`val` != null) {
//            mPlannedPurchaseItemsQuantity = Int(`val`) // sqlliteに保存する場合、BigDecimal.toString()で、復元する場合、new BigDecimal()で行う。
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_PLANNED_DELIVERIES_QUANTITY]
//        if (`val` != null) {
//            mPlannedDeliveriesQuantity = Int(`val`) // sqlliteに保存する場合、BigDecimal.toString()で、復元する場合、new BigDecimal()で行う。
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_UNIT]
//        if (`val` != null) {
//            unit = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_DEL_FLG]
//        if (`val` != null) {
//            mDelFlg = DatabaseContract.Stock.TRUE.equals(`val`)
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_CREATED_AT]
//        if (`val` != null) {
//            mCreatedAt = `val`.toLong()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_PLACE]
//        if (`val` != null) {
//            place = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_STATE]
//        if (`val` != null) {
//            state = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_CODE]
//        if (`val` != null) {
//            this.code = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_ETC]
//        if (`val` != null) {
//            etc = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_CREATE_USER_ID]
//        if (`val` != null) {
//            mCreateUserId = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_CREATE_USER_NAME]
//        if (`val` != null) {
//            mCreateUserName = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_UPDATE_USER_ID]
//        if (`val` != null) {
//            mUpdateUserId = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_UPDATE_USER_NAME]
//        if (`val` != null) {
//            updateUserName = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_COMPANY_ID]
//        if (`val` != null) {
//            mCompanyId = `val`.toLong()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_USER_GROUP]
//        if (`val` != null) {
//            userGroup = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_OPTIONAL_ATTRIBUTES]
//        if (`val` != null) {
//            mOptionalAttribute = OptionalAttribute.Companion.merge(mOptionalAttribute, OptionalAttribute.Companion.parseAttribute(`val`))
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_PHOTO_FILE_NAME]
//        if (`val` != null) {
//            photoFileName = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE]
//        if (`val` != null) {
//            mSyncState = `val`.toInt()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_CHECKED_AT]
//        if (`val` != null) {
//            mCheckedAt = `val`.toLong()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_UPDATED_AT_WHEN_SYNC]
//        if (`val` != null) {
//            mUpdatedAtWhenSync = `val`.toLong()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_RECEIVE_ZAICON]
//        if (`val` != null) {
//            mIsReceiveZaicon = DatabaseContract.Stock.TRUE.equals(`val`)
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_IS_QUANTITY_AUTO_CONVERSION_BY_UNIT]
//        if (`val` != null) {
//            isQuantityAutoConversionByUnit = DatabaseContract.Stock.TRUE.equals(`val`)
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_QUANTITY_AUTO_CONVERSION_BY_UNIT_NAME]
//        if (`val` != null) {
//            quantityAutoConversionByUnitName = `val`
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_QUANTITY_AUTO_CONVERSION_BY_UNIT_FACTOR]
//        if (`val` != null) {
//            mQuantityAutoConversionByUnitFactor = Int(`val`) // sqlliteに保存する場合、BigDecimal.toString()で、復元する場合、new BigDecimal()で行う。
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_OPTIMAL_INVENTORY_LEVEL]
//        if (`val` != null) {
//            mOptimalInventoryLevel = Int(`val`)
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_INVENTORY_ID]
//        if (`val` != null) {
//            inventoryId = `val`.toInt()
//        }
//        `val` = columnMap[DatabaseContract.Stock.COLUMN_NAME_INVENTORY_MASTER_ID]
//        if (`val` != null) {
//            inventoryMasterId = `val`.toInt()
//        }
//
//        // @formatter:on
//    }
//
//    fun getPropertyForServerData(context: android.content.Context?): Map<String, RequestBody> {
//        // Stockではこのメソッドは使わない。
//        throw java.lang.RuntimeException("Stockではこのメソッドは使わない")
//    }
//
//    val propertyForServerWithoutImage: Map<String, Any>
//        /**
//         * ローカルの在庫データをサーバ送信用に変換する
//         */
//        get() {
//            val parameterMap: java.util.HashMap<String, RequestBody> =
//                java.util.HashMap<String, RequestBody>()
//            // ユーザー別表示禁止項目が設定されているので、with_hidden_attributes=1を設定する
//            if (!UserHiddenAttribute.getAllHiddenAttributeDisplayNames(null).isEmpty()) {
//                parameterMap.put("with_hidden_attributes", toRequestBodyString("1"))
//            }
//            // @formatter:off
//            if (mCommonId != null) {
//                parameterMap.put("inventory[common_id]", toRequestBodyString(mCommonId))
//            }
//            parameterMap.put("inventory[title]", toRequestBodyString(MiscUtils.emptyStringIfNull(title)))
//            if (mUpdatedAt != null) {
//                parameterMap.put("inventory[updated_at]", toRequestBodyString(MiscUtils.formatIso8601ExtendedFormat(mUpdatedAt)))
//            }
//            if (mUpdateDate != null) {
//                parameterMap.put("inventory[update_date]", toRequestBodyString(MiscUtils.formatIso8601ExtendedFormat(mUpdateDate)))
//            }
//            parameterMap.put("inventory[quantity]", toRequestBodyString(if (mQuantity == null)"" else MiscUtils.formatQuantityForRails(mQuantity)))
//            if (mOrderPoint != null) {
//                parameterMap.put("inventory[quantity_management_attributes][order_point_quantity]", toRequestBodyString(MiscUtils.formatQuantityForRails(mOrderPoint)))
//                parameterMap.put("inventory[quantity_management_attributes][warning_disabled]", toRequestBodyString(if (mIsCheckedOrderPointWarningDisabled)"1" else "0"))
//            } else {
//                parameterMap.put("inventory[quantity_management_attributes][order_point_quantity]", toRequestBodyString(""))
//            }
//            if (mOptimalInventoryLevel != null) {
//                parameterMap.put("inventory[quantity_management_attributes][optimal_inventory_level]", toRequestBodyString(MiscUtils.formatQuantityForRails(mOptimalInventoryLevel)))
//            } else {
//                parameterMap.put("inventory[quantity_management_attributes][optimal_inventory_level]", toRequestBodyString(""))
//            }
//            if (mLogicalQuantity != null) {
//                // 予定フリー在庫系のカラムは同期周りのバグ対策としてサーバに送信しない
//            }
//            if (mPlannedPurchaseItemsQuantity != null) {
//                // 予定フリー在庫系のカラムは同期周りのバグ対策としてサーバに送信しない
//            }
//            if (mPlannedDeliveriesQuantity != null) {
//                // 予定フリー在庫系のカラムはは同期周りのバグ対策としてサーバに送信しない
//            }
//            if (mDelFlg != null) {
//                parameterMap.put("inventory[del_flg]", toRequestBodyString(if (mDelFlg)"1" else "0"))
//            }
//            parameterMap.put("inventory[state]", toRequestBodyString(MiscUtils.emptyStringIfNull(state)))
//            parameterMap.put("inventory[place]", toRequestBodyString(MiscUtils.emptyStringIfNull(place)))
//            parameterMap.put("inventory[code]", toRequestBodyString(MiscUtils.emptyStringIfNull(this.code)))
//            parameterMap.put("inventory[etc]", toRequestBodyString(MiscUtils.emptyStringIfNull(etc)))
//            if (mCreateUserId != null) {
//                parameterMap.put("inventory[create_user_id]", toRequestBodyString(mCreateUserId))
//            }
//            if (mUpdateUserId != null) {
//                parameterMap.put("inventory[update_user_id]", toRequestBodyString(mUpdateUserId))
//            }
//            if (mCreatedAt != null) {
//                parameterMap.put("inventory[created_at]", toRequestBodyString(MiscUtils.formatIso8601ExtendedFormat(mCreatedAt)))
//            }
//            parameterMap.put("inventory[unit]", toRequestBodyString(MiscUtils.emptyStringIfNull(unit)))
//            parameterMap.put("inventory[category]", toRequestBodyString(if (mCategory == null)"" else serializedCategory))
//            if (mCreateUserName != null) {
//                parameterMap.put("inventory[create_user_name]", toRequestBodyString(mCreateUserName))
//            }
//            if (updateUserName != null) {
//                parameterMap.put("inventory[update_user_name]", toRequestBodyString(updateUserName))
//            }
//            if (mCompanyId != null) {
//                parameterMap.put("inventory[company_id]", toRequestBodyString(java.lang.String.valueOf(mCompanyId)))
//            }
//            parameterMap.put("inventory[optional_attributes]", toRequestBodyString(OptionalAttribute.Companion.getSerializedAttribute(mOptionalAttribute)))
//            if (userGroup != null) {
//                parameterMap.put("inventory[user_group]", toRequestBodyString(userGroup))
//            }
//            if (mCheckedAt != null) {
//                parameterMap.put("inventory[stocktake_attributes][checked_at]", toRequestBodyString(MiscUtils.formatIso8601ExtendedFormat(mCheckedAt)))
//            }
//            // サムネイルファイル名が削除されている場合は削除フラグを設定する
//            parameterMap.put("inventory[remove_item_image]", toRequestBodyString(if (photoFileName == null)"1" else "0"))
//            // @formatter:on
//            return parameterMap
//        }
//
//    /**
//     * 写真ファイルがアップロード済みか。
//     *
//     * @return
//     */
//    private fun isPhotoUploaded(context: android.content.Context): Boolean {
//        val uploadSignFile: java.io.File = FileConvention.getPhotoFileUploadedSignFilePath(
//            context, mCommonId,
//            photoFileName
//        )
//        return uploadSignFile.exists()
//    }
//
//    val contentUri: android.net.Uri
//        get() = DatabaseContract.Stock.CONTENT_URI
//
//    val contentUrlForServer: String
//        get() {
//            throw java.lang.RuntimeException("このメソッドはStockでは使わない")
//        }
//
//    val updateUrlForServer: String
//        get() {
//            throw java.lang.RuntimeException("このメソッドはStockでは使わない")
//        }
//
//    val createUrlForServer: String
//        get() {
//            throw java.lang.RuntimeException("このメソッドはStockでは使わない")
//        }
//
//    /**
//     * ローカルDBから再度取得する
//     *
//     * @param context
//     * @return
//     */
//    fun reload(context: android.content.Context): Stock? {
//        return get(context, getCommonId())
//    }
//
//    /**
//     * 画像をダウンロードする。ダウンロード中に在庫データや写真データが更新されることを想定する。
//     *
//     * @param context
//     */
//    @Throws(java.io.IOException::class)
//    private fun downloadPhotoAsynchronous(context: android.content.Context) {
//        if (android.text.TextUtils.isEmpty(photoFileName) || mDelFlg) {
//            return
//        }
//
//        if (!photoFileName!!.startsWith("http")) {
//            return
//        }
//
//        // 画像ダウンロード
//        val fileName: String = android.net.Uri.parse(photoFileName).getLastPathSegment()
//        val photoFile: java.io.File = FileConvention.getPhotoFilePath(context, mCommonId, fileName)
//        val tempPhotoFile: java.io.File =
//            FileConvention.getPhotoFilePath(context, mCommonId, "__TEMP__")
//        tempPhotoFile.getParentFile().mkdirs()
//        if (BuildConfig.DEBUG) {
//            photoFileName = photoFileName!!.replace("http://localhost:3000", BuildConfig.URL_HOST)
//        }
//
//        photoFileDownload(photoFileName, tempPhotoFile)
//
//        if (!tempPhotoFile.exists() || tempPhotoFile.length() <= 0) {
//            return
//        }
//
//        val values: ContentValues = ContentValues()
//        values.put(DatabaseContract.Stock.COLUMN_NAME_PHOTO_FILE_NAME, fileName)
//
//        if (BuildConfig.DEBUG) {
//            photoFileName = photoFileName!!.replace(BuildConfig.URL_HOST, "http://localhost:3000")
//        }
//
//        // ファイル名がダウンロード開始時と同じことを確認
//        // して、ファイル名のみupdate（違ったら何もしない
//        if (0 == context.getContentResolver().update(
//                contentUri, values,
//                (commonIdColumnName + "=? AND "
//                        + DatabaseContract.Stock.COLUMN_NAME_PHOTO_FILE_NAME).toString() + "=?",
//                arrayOf<String?>(getCommonId(), photoFileName)
//            )
//        ) {
//            // ファイル名がダウンロード開始時と異なる場合
//            // 何もせず終了する
//            tempPhotoFile.delete()
//        } else {
//            // ファイル名がダウンロード開始時と同じ場合
//            // 画像を更新・アップロードを記録
//            tempPhotoFile.renameTo(photoFile)
//            recordPhotoUploaded(context, fileName)
//            photoFileName = fileName
//        }
//    }
//
//    /**
//     * 写真をダウンロードして保存する
//     *
//     * @param photoFilePath
//     * @param tempPhotoFile
//     */
//    private fun photoFileDownload(photoFilePath: String?, tempPhotoFile: java.io.File) {
//        val url: String?
//        if (photoFilePath!!.startsWith("http")) {
//            if (BuildConfig.DEBUG && !BuildConfig.URL_HOST.startsWith("https://twics")) {
//                // ローカルのUIテストで使用する
//                // railsの開発環境の写真URLが'http://localhost:7000/uploads/inventory/item_image/'を返すようになっているので、変更する
//                url = photoFilePath.replace("http://localhost:7000", BuildConfig.URL_HOST)
//            } else {
//                url = photoFilePath
//            }
//        } else {
//            url = BuildConfig.URL_HOST + photoFilePath
//        }
//
//        val client: ApiClient = ApiClientManager.apiClient()
//        val call: Call<ResponseBody> = client.get(url)
//
//        try {
//            val response: Response<ResponseBody> = call.execute()
//            if (response.isSuccessful()) {
//                writeResponseBodyToFile(response.body(), tempPhotoFile)
//            }
//        } catch (e: java.io.IOException) {
//            // 何もしない
//        }
//    }
//
//    /**
//     * サーバからのレスポンスからファイルに保存する
//     *
//     * @param body
//     * @param file
//     * @throws IOException
//     */
//    @Throws(java.io.IOException::class)
//    fun writeResponseBodyToFile(body: ResponseBody?, file: java.io.File?) {
//        var inputStream: java.io.InputStream? = null
//        var outputStream: java.io.OutputStream? = null
//
//        if (body == null) {
//            return
//        }
//
//        try {
//            val fileReader = ByteArray(4096)
//            inputStream = body.byteStream()
//            outputStream = java.io.FileOutputStream(file)
//
//            while (true) {
//                val read: Int = inputStream.read(fileReader)
//
//                if (read == -1) {
//                    break
//                }
//
//                outputStream.write(fileReader, 0, read)
//            }
//
//            outputStream.flush()
//        } finally {
//            if (inputStream != null) {
//                inputStream.close()
//            }
//
//            if (outputStream != null) {
//                outputStream.close()
//            }
//        }
//    }
//
//    /**
//     * 在庫をサーバーにアップロードする.
//     *
//     * @param context
//     * @throws MalformedURLException
//     * @throws ProtocolException
//     * @throws UnsupportedEncodingException
//     * @throws FileNotFoundException
//     * @throws IOException
//     * @throws ZaicoHttpException
//     */
//    @Throws(
//        java.io.IOException::class,
//        ZaicoHttpException::class,
//        JSONException::class,
//        java.text.ParseException::class,
//        java.lang.IllegalAccessException::class,
//        java.lang.InstantiationException::class
//    )
//    fun uploadStockToServer(context: android.content.Context) {
//        val response: String
//        // ２ヶ月以上前の更新データはサーバに反映させない。
//        // 同じcommon_idがすでに存在するなどの不正なデータが存在し409で更新できず、同期し続けてサーバに負荷をかけている可能性があるので。
//        if (isUpdatedBeforeTwoMonthAgo()) {
//            return
//        }
//
//        if (mSyncState === DatabaseContract.Stock.INSERT) {
//            if (!isDeleted()) {
//                response = insertToServer(context)
//                // バグ調査:#683 vvvvvvvvvvvvvvvvvv
//                try {
//                    try {
//                        val item_image_url =
//                            (org.json.JSONObject(response).getJSONObject("inventory")
//                                .get("item_image") as org.json.JSONObject).get("url") as String
//                        FirebaseCrashlytics.getInstance()
//                            .setCustomKey("item_image_url2", item_image_url)
//                    } catch (e: JSONException) {
//                        FirebaseCrashlytics.getInstance().setCustomKey(
//                            "item_image_url2",
//                            "response:$response"
//                        )
//                    }
//                } catch (e: java.lang.Exception) {
//                }
//                // バグ調査:#683 ^^^^^^^^^^^^^^^^^^^
//                if (!android.text.TextUtils.isEmpty(photoFileName)) {
//                    val returnStock: Stock
//                    if (org.json.JSONObject(response).has("inventory")) {
//                        // 新プランリリース後イキ updateの場合と場合分け統合
//                        val jsonObject: org.json.JSONObject =
//                            org.json.JSONObject(response).getJSONObject("inventory")
//                        returnStock = toInventoryObject(jsonObject, Stock::class.java) as Stock
//                    } else {
//                        // 新プランリリース後削除
//                        returnStock = toInventoryObject(
//                            org.json.JSONObject(response),
//                            Stock::class.java
//                        ) as Stock
//                    }
//                    changePhotoFileNameAfterUpload(context, returnStock)
//                }
//            }
//        } else {
//            response = updateToServerByCommonId(context)
//            if (!android.text.TextUtils.isEmpty(photoFileName)) {
//                // バグ調査:#641 vvvvvvvvvvvvvvvvvv
//                try {
//                    try {
//                        val item_image_url =
//                            (org.json.JSONObject(response).getJSONObject("inventory")
//                                .get("item_image") as org.json.JSONObject).get("url") as String
//                        FirebaseCrashlytics.getInstance()
//                            .setCustomKey("item_image_url", item_image_url)
//                    } catch (e: JSONException) {
//                        FirebaseCrashlytics.getInstance().setCustomKey(
//                            "item_image_url",
//                            "response:$response"
//                        )
//                    }
//                } catch (e: java.lang.Exception) {
//                }
//                // バグ調査:#641 ^^^^^^^^^^^^^^^^^^^
//                val jsonObject: org.json.JSONObject =
//                    org.json.JSONObject(response).getJSONObject("inventory")
//                val returnStock = toInventoryObject(jsonObject, Stock::class.java) as Stock
//                changePhotoFileNameAfterUpload(context, returnStock)
//            }
//        }
//        setSyncStateNothingAndResetUpdatedAtWhenSync(context)
//    }
//
//    // ファイルを一緒にアップロードする際に、@PartMapに含めてアップロードする実装ができなかった。
//    // そのため、insertToServerをoverrideして、変更している。
//    @Throws(java.io.IOException::class)
//    protected fun insertToServer(context: android.content.Context): String {
//        FirebaseCrashlytics.getInstance().setCustomKey("CLASS", this.javaClass.getSimpleName())
//        FirebaseCrashlytics.getInstance().setCustomKey("SYNC_ACTION", "Insert")
//        FirebaseCrashlytics.getInstance().setCustomKey("COMMON_ID", getCommonId())
//
//        // アップロード通信を同期で実行
//        val client: ApiClient = ApiClientManager.apiClient()
//        val call: Call<ResponseBody> =
//            client.postInventories(propertyForServerWithoutImage, getPhotoFilePart(context))
//        val response: Response<ResponseBody> = call.execute()
//
//        throwIfApiCallFailed(response)
//
//        // stringメソッドによりResponseBodyはCloseされる。
//        return response.body().string()
//    }
//
//    /**
//     * アップロード用に写真ファイルのMultipartBody#Partを取得する。写真がない在庫、アップロード済み、ファイルが見つからない、削除ずみの在庫の場合はnullを返す。
//     *
//     * @param context
//     * @return
//     */
//    private fun getPhotoFilePart(context: android.content.Context): MultipartBody.Part? {
//        val photoFile: java.io.File? = getPhotoFileForUpload(context)
//        var photoFilePart: MultipartBody.Part? = null
//        if (photoFile != null) {
//            val fileReqBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), photoFile)
//            photoFilePart = MultipartBody.Part.createFormData(
//                "inventory[item_image]",
//                photoFile.getName(),
//                fileReqBody
//            )
//        }
//        return photoFilePart
//    }
//
//    /**
//     * 写真のファイルを取得する。ただし、すでにファイルをアップロード済みならnullを返す。
//     *
//     * @param context
//     * @return
//     */
//    private fun getPhotoFileForUpload(context: android.content.Context): java.io.File? {
//        if ((photoFileName != null) && !mDelFlg && !isPhotoUploaded(context)) {
//            val file: java.io.File = FileConvention.getPhotoFilePath(
//                context, mCommonId,
//                photoFileName
//            )
//            if (file.exists()) {
//                return file
//            } else {
//                FirebaseCrashlytics.getInstance()
//                    .recordException(java.io.FileNotFoundException("デバイスにアップロードしようとする写真ファイルが見つかりません"))
//            }
//        }
//        return null
//    }
//
//    /**
//     * サーバーに在庫情報のupdateをかける.サーバーdbのid不要.
//     *
//     * @param context
//     * @throws FileNotFoundException
//     * @throws IOException
//     */
//    @Throws(java.io.IOException::class)
//    private fun updateToServerByCommonId(context: android.content.Context): String {
//        FirebaseCrashlytics.getInstance().setCustomKey("CLASS", this.javaClass.getSimpleName())
//        FirebaseCrashlytics.getInstance().setCustomKey("COMMON_ID", getCommonId())
//
//        val client: ApiClient = ApiClientManager.apiClient()
//        val call: Call<ResponseBody>
//
//        if (!isDeleted()) {
//            // 更新
//            FirebaseCrashlytics.getInstance().setCustomKey("SYNC_ACTION", "Update")
//
//            // アップロード通信を同期で実行
//            call = client.putInventories(
//                mCommonId,
//                propertyForServerWithoutImage, getPhotoFilePart(context)
//            )
//        } else {
//            // 削除
//            FirebaseCrashlytics.getInstance().setCustomKey("SYNC_ACTION", "Delete")
//
//            // アップロード通信を同期で実行
//            call = client.deleteInventories(mCommonId)
//        }
//
//        val response: Response<ResponseBody> = call.execute()
//        throwIfApiCallFailed(response)
//
//        // stringメソッドによりResponseBodyはCloseされる。
//        return response.body().string()
//    }
//
//    /**
//     * 写真ファイルアップロード後に返り値から画像のファイル名を変更する。また、アップロード済みであることを記録する.
//     *
//     * @param context
//     * @throws IOException
//     */
//    @Throws(java.io.IOException::class)
//    private fun changePhotoFileNameAfterUpload(
//        context: android.content.Context,
//        returnStock: Stock
//    ) {
//        // TODO returnStock.mPhotoFileNameがnullになる条件がわからない。このままでは画像をアップロードし続けてしまうかもしれない。
//        // 現在の方式をやめて、volleyを使う方式に変えた方がいい
//        if (!android.text.TextUtils.isEmpty(photoFileName) && !android.text.TextUtils.isEmpty(
//                returnStock.photoFileName
//            )
//        ) {
//            val returnPhotoFileName: String =
//                android.net.Uri.parse(returnStock.photoFileName).getLastPathSegment()
//            if (!android.text.TextUtils.equals(returnPhotoFileName, photoFileName)) {
//                if (changePhotoFileName(context, returnPhotoFileName)) {
//                    recordPhotoUploaded(context, photoFileName)
//                }
//            }
//        }
//    }
//
//    /**
//     * 写真がアップロードされたことを記録する.
//     *
//     * @param context
//     * @throws IOException
//     */
//    @Throws(java.io.IOException::class)
//    private fun recordPhotoUploaded(context: android.content.Context, filename: String?) {
//        val uploadSignFile: java.io.File =
//            FileConvention.getPhotoFileUploadedSignFilePath(context, mCommonId, filename)
//        if (!uploadSignFile.exists()) {
//            uploadSignFile.createNewFile()
//        }
//    }
//
//    /**
//     * 写真のファイル名を変更し、ファイル名だけをローカルDBに保存する.
//     *
//     * @param context
//     * @param newPhotoFileName
//     */
//    @Throws(java.io.IOException::class)
//    private fun changePhotoFileName(
//        context: android.content.Context,
//        newPhotoFileName: String
//    ): Boolean {
//        if (!isSaved()) {
//            return false
//        }
//
//        val currentPhotoFile: java.io.File = FileConvention.getPhotoFilePath(
//            context, getCommonId(),
//            photoFileName
//        )
//        val newPhotoFile: java.io.File =
//            FileConvention.getPhotoFilePath(context, getCommonId(), newPhotoFileName)
//        if (!currentPhotoFile.exists()) return false
//        if (newPhotoFile.exists()) {
//            newPhotoFile.delete()
//        }
//
//        org.apache.commons.io.FileUtils.moveFile(currentPhotoFile, newPhotoFile)
//        photoFileName = newPhotoFileName
//
//        val values: ContentValues = ContentValues()
//        values.put(DatabaseContract.Stock.COLUMN_NAME_PHOTO_FILE_NAME, this.photoFileName)
//
//        return 1 == context.getContentResolver().update(
//            contentUri,
//            values,
//            commonIdColumnName + "=?",
//            arrayOf<String>(
//                getCommonId(),
//            )
//        )
//    }
//
//    /**
//     * 同期状態をupdateに変更する.すでにupdateなら何もしない.
//     *
//     * @param context
//     * @return
//     */
//    private fun setSyncStateUpdate(context: android.content.Context): Boolean {
//        if (mSyncState === DatabaseContract.Stock.UPDATE) {
//            return false
//        }
//
//        mSyncState = DatabaseContract.Stock.UPDATE
//        return update(context)
//    }
//
//    /**
//     * 同期状態をnothingに変更する.また、UpdatedAtWhenSyncをUpdatedAtと同じ値に変更して保存する.
//     *
//     * @param context
//     * @return
//     */
//    private fun setSyncStateNothingAndResetUpdatedAtWhenSync(context: android.content.Context): Boolean {
//        mUpdatedAtWhenSync = mUpdatedAt
//        mSyncState = NOTHING
//        return update(context)
//    }
//
//    /**
//     * 写真を含めてを保存する.InventoryObject#saveでは写真ファイルは保存されない.
//     *
//     * @param context   コンテキスト.
//     * @param operation 履歴に保存するInventoryTransactionOperation
//     * @param fileName  ファイル名
//     * @param srcFile   写真ファイル.実行後、ファイルは削除される.
//     * @throws IOException
//     */
//    @Throws(java.io.IOException::class)
//    fun saveWithPhoto(
//        context: android.content.Context,
//        operation: InventoryTransactionOperation?,
//        fileName: String?,
//        srcFile: java.io.File?
//    ) {
//        val file: java.io.File = FileConvention.getPhotoFilePath(context, getCommonId(), fileName)
//        if (file.exists()) {
//            if (file.delete()) {
//                recordNeedPhotoUpload(context, fileName)
//            }
//        } else {
//            file.getParentFile().mkdirs()
//        }
//        org.apache.commons.io.FileUtils.moveFile(srcFile, file)
//        photoFileName = fileName
//        saveWithInventoryTransaction(context, operation, "")
//    }
//
//    private fun recordNeedPhotoUpload(context: android.content.Context, fileName: String?) {
//        val signFile: java.io.File =
//            FileConvention.getPhotoFileUploadedSignFilePath(context, getCommonId(), fileName)
//        if (signFile.exists()) {
//            signFile.delete()
//        }
//    }
//
//    /**
//     * ローカルに保存されている写真ファイルを削除する.保存されていない場合は何もしない.InventoryObject#
//     * deleteでは写真ファイルは削除されない.
//     *
//     * @param context コンテキスト
//     */
//    fun deletePhoto(context: android.content.Context) {
//        if (!android.text.TextUtils.isEmpty(photoFileName)) {
//            deletePhotoFile(context)
//            photoFileName = null
//        }
//    }
//
//    /**
//     * 写真ファイルを削除する.
//     *
//     * @param context
//     */
//    private fun deletePhotoFile(context: android.content.Context) {
//        val file: java.io.File = FileConvention.getPhotoFilePath(
//            context, getCommonId(),
//            photoFileName
//        )
//        if (file.exists()) {
//            file.delete()
//            recordNeedPhotoUpload(context, photoFileName)
//            if (file.getParentFile().list().size == 0) {
//                file.getParentFile().delete()
//            }
//        }
//    }
//
//    protected val commonIdColumnName: String
//        get() = DatabaseContract.Stock.COLUMN_NAME_COMMON_ID
//
//    protected val nameColumnName: String
//        get() = DatabaseContract.Stock.COLUMN_NAME_TITLE
//
//    protected val falseValue: String
//        get() = DatabaseContract.Stock.FALSE
//
//    protected val delFlgColumnName: String
//        get() = DatabaseContract.Stock.COLUMN_NAME_DEL_FLG
//
//    protected fun createTimestamp(context: android.content.Context?) {
//        super.createTimestamp(context)
//        mUpdateDate = MiscUtils.getCurrentTimeInSec()
//        val accountManager: AccountManager = AccountManager(context)
//        mCreateUserName = accountManager.getUserName()
//        updateUserName = accountManager.getUserName()
//        if (android.text.TextUtils.isEmpty(userGroup)) {
//            userGroup = accountManager.getUserGroup()
//        }
//    }
//
//    protected fun updateTimestamp(context: android.content.Context?) {
//        super.updateTimestamp(context)
//        updateUserName = AccountManager(context).getUserName()
//        mUpdateDate = MiscUtils.getCurrentTimeInSec()
//    }
//
//    /**
//     * 写真のファイルを取得する.
//     *
//     * @param context コンテキスト
//     * @return ファイル
//     */
//    fun getPhotoFile(context: android.content.Context?): java.io.File? {
//        if (photoFileName != null) {
//            return FileConvention.getPhotoFilePath(context, getCommonId(), photoFileName)
//        }
//        return null
//    }
//
//    /**
//     * InventoryTransactionOperationが保存されないので、saveWithInventoryTransactionを使ってください。
//     * InventoryObject.saveを継承しているので、削除ではなく@Deprecatedをつけることで、Stock.saveが非推奨であることを明示。
//     * InventoryObject.saveやCategory.saveは引き続き使うため。
//     *
//     * @param context コンテキスト
//     */
//    @Deprecated("")
//    fun save(context: android.content.Context?) {
//        throw java.lang.UnsupportedOperationException("Stock.saveメソッドは使用禁止です")
//    }
//
//    /**
//     * 履歴無しで保存.
//     *
//     * @param context
//     */
//    fun saveWithoutHistory(context: android.content.Context) {
//        if (!isSaved() && !isUnderLimitOnCountOfStocks(context)) {
//            throw java.lang.RuntimeException("登録限度数を超えて在庫を登録しようとした")
//        }
//
//        if (mDelFlg && !android.text.TextUtils.isEmpty(photoFileName)) {
//            deletePhotoFile(context)
//        }
//
//        if (mSyncState === NOTHING) {
//            mSyncState =
//                if (isSaved()) DatabaseContract.Stock.UPDATE else DatabaseContract.Stock.INSERT
//        }
//
//        if (mDelFlg && mSyncState === DatabaseContract.Stock.INSERT) {
//            mSyncState = NOTHING
//        }
//
//        // 数量が発注点を超えていたら「警告オフ」をオフにする
//        if ((quantity != null) && (orderPoint != null) && (quantity.compareTo(orderPoint) > 0)) {
//            setIsOrderPointWarningDisabled(false)
//        }
//
//        super.save(context)
//    }
//
//    /**
//     * 追加項目の値を項目名から取得する。
//     *
//     * @param title 追加項目名.
//     * @return 値.見つからない場合null.
//     */
//    fun findOptionalAttribute(title: String?): String? {
//        if (mOptionalAttribute == null || mOptionalAttribute.get(title) == null) {
//            return null
//        }
//        return java.util.Objects.requireNonNull<Any>(mOptionalAttribute.get(title)).getValue()
//    }
//
//    /**
//     * 追加項目の値を追加.
//     *
//     * @param title   追加項目のタイトル.
//     * @param content 内容.内容が空文字、もしくはnullの場合、当該追加項目はないと看做して削除.
//     */
//    fun setOptionalAttribute(title: String?, content: String?) {
//        if (mOptionalAttribute == null) {
//            mOptionalAttribute = java.util.HashMap<String, OptionalAttribute>()
//        }
//
//        val currentAttribute: OptionalAttribute = mOptionalAttribute.get(title)
//        if (currentAttribute == null && content != null) {
//            // 新規の追加項目を登録する場合
//            mOptionalAttribute.put(title, OptionalAttribute(title, null, content))
//        } else if (currentAttribute != null) {
//            // 既存の追加項目を上書きする場合
//            currentAttribute.setValue(content)
//            mOptionalAttribute.put(title, currentAttribute)
//        }
//
//        if (mOptionalAttribute.isEmpty()) {
//            mOptionalAttribute = null
//        }
//    }
//
//    /**
//     * 新変更履歴に対応した形で保存する。
//     *
//     * @param context
//     * @param operation      InventoryTransactionに保存するOperation
//     * @param oldHistoryMemo 旧変更履歴を使っているcompany用のメモの文字列
//     */
//    fun saveWithInventoryTransaction(
//        context: android.content.Context,
//        operation: InventoryTransactionOperation?,
//        oldHistoryMemo: String?
//    ) {
//        saveWithoutHistory(context)
//        StockHistoryQueue.push(context, this, operation, oldHistoryMemo)
//    }
//
//    val localPhotoFileName: String
//        /**
//         * ローカルにのみ保存された写真ファイルのファイル名を返す。
//         *
//         * @return
//         */
//        get() = getCommonId() + ".jpg"
//
//    /**
//     * 写真を含めて削除.
//     *
//     * @param context
//     */
//    fun delete(context: android.content.Context) {
//        deletePhoto(context)
//        mDelFlg = true
//
//        if (isSaved()) {
//            // 削除の場合、履歴はサーバ側で作成するので履歴は送らない。
//            saveWithoutHistory(context)
//        }
//    }
//
//    fun stocktakeWithSave(context: android.content.Context, isStocktakenOnStockList: Boolean) {
//        mCheckedAt = MiscUtils.getCurrentTimeInSec()
//        val historyMemoResId: Int
//        val inventoryTxOperation: InventoryTransactionOperation
//        if (isStocktakenOnStockList) {
//            inventoryTxOperation = InventoryTransactionOperation.initForInventoriesStocktakeUpdate()
//            historyMemoResId = R.string.message_on_history_for_stocktake_in_stocklist
//        } else {
//            inventoryTxOperation = InventoryTransactionOperation.initForScanningStocktakeCreate()
//            historyMemoResId = R.string.label_stocktake_by_scanning_on_history
//        }
//
//        saveWithInventoryTransaction(
//            context,
//            inventoryTxOperation,
//            context.getString(historyMemoResId)
//        )
//    }
//
//    val checkedAt: Long?
//        get() = mCheckedAt
//
//    val categoryForDisplay: String?
//        /**
//         * カテゴリをカンマ区切りで接続して表示する
//         *
//         * @return
//         */
//        get() {
//            if (this.category == null) {
//                return null
//            } else {
//                return android.text.TextUtils.join(", ", this.category)
//            }
//        }
//
//
//    /**
//     * カテゴリのカンマ区切りを追加する
//     *
//     * @param context
//     * @param stringBuilder
//     * @return
//     */
//    fun addCategoryTextForDisplay(
//        context: android.content.Context?,
//        stringBuilder: java.lang.StringBuilder
//    ) {
//        val categories: List<String> = Category.getAllName(context)
//        if (mCategory == null || categories == null) {
//            return
//        }
//
//        var appended = false
//        for (category: String? in categories) {
//            if (mCategory.contains(category)) {
//                stringBuilder.append(category).append(",")
//                appended = true
//            }
//        }
//
//        if (appended) {
//            stringBuilder.setLength(stringBuilder.length - 1)
//        }
//    }
//
//    fun getCategoriesForDisplay(context: android.content.Context?): List<String> {
//        val result: java.util.ArrayList<String> = java.util.ArrayList<String>()
//
//        val categories: List<String> = Category.getAllName(context)
//        if (mCategory == null || categories == null) {
//            return result
//        }
//
//        for (category: String? in categories) {
//            if (mCategory.contains(category)) {
//                result.add(category)
//            }
//        }
//
//        return result
//    }
//
//
//    @Throws(java.io.IOException::class)
//    protected fun updateToServer(context: android.content.Context?) {
//        throw java.lang.RuntimeException("このメソッドは使用しない")
//    }
//
//    /**
//     * 在庫データがサーバーにアップロード（insertもしくはupdate）する必要がある状態か。
//     *
//     * @return
//     */
//    fun shouldUpload(): Boolean {
//        return (mSyncState === DatabaseContract.Stock.UPDATE || mSyncState === DatabaseContract.Stock.INSERT) && !isDeleted()
//    }
//
//    /**
//     * スネークケースの項目名から表示用の値を取得する
//     *
//     * @param snakeCaseFieldName
//     * @return 表示用の値、想定外の項目名の場合は空文字
//     */
//    fun getDisplayValueByFieldName(snakeCaseFieldName: String?): String? {
//        when (snakeCaseFieldName) {
//            "title" -> return this.title
//            "quantity" -> if (android.text.TextUtils.isEmpty(this.unit)) {
//                return MiscUtils.formatQuantityWithGrouping(this.mQuantity)
//            } else {
//                return MiscUtils.formatQuantityWithGrouping(this.mQuantity) + " " + this.unit
//            }
//
//            "code" -> return this.code
//            "place" -> return this.place
//            "state" -> return this.state
//            "category" -> return this.categoryForDisplay
//            "etc" -> return this.etc
//            "user_group" -> return this.userGroup
//            "update_user_name" -> return this.updateUserName
//            "order_point" -> return MiscUtils.formatQuantityWithGrouping(this.mOrderPoint)
//            "order_point_quantity" -> return MiscUtils.formatQuantityWithGrouping(this.mOrderPoint)
//            "logical_quantity" -> return MiscUtils.formatQuantityWithGrouping(this.mLogicalQuantity)
//            "updated_at" -> return MiscUtils.formatDisplayTime(this.getUpdatedAt())
//            "update_date" -> return MiscUtils.formatDisplayTime(this.updateDate)
//            "checked_at" -> return MiscUtils.formatDisplayTime(this.checkedAt)
//            "optimal_inventory_level" -> if (android.text.TextUtils.isEmpty(this.unit)) {
//                return MiscUtils.formatQuantityWithGrouping(this.mOptimalInventoryLevel)
//            } else {
//                val value: String =
//                    MiscUtils.formatQuantityWithGrouping(this.mOptimalInventoryLevel)
//                if (android.text.TextUtils.isEmpty(value)) {
//                    return ""
//                } else {
//                    return value + " " + this.unit
//                }
//            }
//
//            else -> return ""
//        }
//    }
//
//    val isUnderQuantityOrderPoint: Boolean
//        /**
//         * 現在数量が発注点を下回っているかどうかを返す。
//         * 警告をオフにするかどうかの値は考慮せずに純粋に数量が発注点を下回っているかを返す。
//         *
//         * @return 現在数量が発注点を下回っているかどうか
//         */
//        get() = (mQuantity != null) && (mOrderPoint != null) && (mQuantity.compareTo(mOrderPoint) <= 0)
//
//    val isUnderLogicalQuantityOrderPoint: Boolean
//        /**
//         * 予定フリー在庫数が発注点を下回っているかどうか
//         */
//        get() = (mLogicalQuantity != null) && (mOrderPoint != null) && (mLogicalQuantity.compareTo(
//            mOrderPoint
//        ) <= 0)
//
//    /**
//     * 指定された開始日後に棚卸されたかを返す
//     *
//     * @param stocktakeStartTime 棚卸開始日
//     * @return
//     */
//    fun isStocktaken(stocktakeStartTime: Long): Boolean {
//        if (isFirstStocktake) {
//            return false
//        }
//
//        return checkedAt!! >= stocktakeStartTime
//    }
//
//    val isFirstStocktake: Boolean
//        /**
//         * まだ棚卸を行っていないかを返す
//         *
//         * @return
//         */
//        get() = checkedAt == null || checkedAt!! <= 0L
//
//    val formattedPurchaseQuantity: String
//        get() {
//            var formattedPurchaseQuantity: String =
//                MiscUtils.formatQuantityWithGrouping(mPlannedPurchaseItemsQuantity)
//
//            if (android.text.TextUtils.isEmpty(formattedPurchaseQuantity)) {
//                formattedPurchaseQuantity = "0"
//            }
//
//            val unit = if (unit == null) "" else unit!!
//            return "$formattedPurchaseQuantity $unit"
//        }
//
//    val formattedPlannedDeliveriesQuantity: String
//        get() {
//            var formattedPlannedDeliveriesQuantity: String =
//                MiscUtils.formatQuantityWithGrouping(mPlannedDeliveriesQuantity)
//            if (android.text.TextUtils.isEmpty(formattedPlannedDeliveriesQuantity)) {
//                formattedPlannedDeliveriesQuantity = "0"
//            }
//            val unit = if (unit == null) "" else unit!!
//            return "$formattedPlannedDeliveriesQuantity $unit"
//        }
//
//    /**
//     * 仕入・納品単価を取得する。仕入・納品単価がない場合はnullを返す。
//     * @param customerName 取引先名。これがnullでない時、「[取引先名]への納品単価/仕入単価」という追加項目の値を利用する。nullの場合は「納品単価/仕入単価」という追加項目の値を利用する
//     * @param isPurchase 仕入単価の場合はtrue。納品単価の場合はfalse。
//     */
//    fun getUnitPrice(customerName: String, isPurchase: Boolean): Int? {
//        // 追加項目がない場合はnull
//        if (optionalAttribute == null) {
//            return null
//        }
//
//        val keyword = if (isPurchase) "仕入単価" else "納品単価"
//
//        // 禁止項目リストを取得
//        val userHiddenAttributes: List<String> =
//            UserHiddenAttribute.getAllHiddenAttributeDisplayNames(null)
//
//        // 取引先ごとの単価を取得する
//        if (!android.text.TextUtils.isEmpty(customerName)) {
//            val unitPriceTitle = customerName + "への" + keyword
//            val unitPriceOptionalAttribute: OptionalAttribute =
//                optionalAttribute.get(unitPriceTitle)
//            // 禁止項目に含まれる場合は取得しない
//            if (unitPriceOptionalAttribute != null && !userHiddenAttributes.contains(unitPriceTitle)) {
//                val unitPrice: Int =
//                    MiscUtils.parsePrice(unitPriceOptionalAttribute.getValue())
//                if (unitPrice != null) {
//                    return unitPrice
//                }
//            }
//        }
//
//        // 取引先ごとの単価がないので、一般の単価を取得する
//        val unitPriceOptionalAttribute: OptionalAttribute = optionalAttribute.get(keyword)
//        // 禁止項目に含まれる場合は取得しない
//        if (unitPriceOptionalAttribute != null && !userHiddenAttributes.contains(keyword)) {
//            return MiscUtils.parsePrice(unitPriceOptionalAttribute.getValue())
//        }
//
//        // 単価未設定の場合はnullを返す
//        return null
//    }
//
//    /**
//     * 引数で取得した数量をまとめ数に換算して取得する。単位による自動換算が有効でない場合はnullを返す
//     */
//    fun calculateQuantitySummary(quantity: Int): Int? {
//        if (!isQuantityAutoConversionByUnit) {
//            return null
//        }
//
//        var summary: Int? = null
//        try {
//            // 余りを切り捨てるため一旦整数で取得し再度セットする
//            summary = Int.valueOf(
//                quantity.divide(
//                    mQuantityAutoConversionByUnitFactor,
//                    1,
//                    java.math.RoundingMode.HALF_DOWN
//                ).toLong()
//            )
//        } catch (ignored: java.lang.Exception) {
//        }
//
//        return summary
//    }
//
//    /**
//     * 引数で取得した数量をまとめ数に換算した余りを取得する。単位による自動換算が有効でない場合はnullを返す
//     */
//    fun calculateQuantitySummaryRemainder(quantity: Int): Int? {
//        if (!isQuantityAutoConversionByUnit) {
//            return null
//        }
//
//        var remainder: Int? = null
//        try {
//            remainder = quantity.remainder(mQuantityAutoConversionByUnitFactor)
//        } catch (ignored: java.lang.Exception) {
//        }
//
//        return remainder
//    }
//
//    val quantitySummary: Int?
//        /**
//         * 数量をまとめ数に換算して取得する。単位による自動換算が有効でない場合はnullを返す
//         */
//        get() {
//            if (!isQuantityAutoConversionByUnit) {
//                return null
//            }
//
//            var summary: Int? = null
//            try {
//                // 余りを切り捨てるため一旦整数で取得し再度セットする
//                summary = Int.valueOf(
//                    mQuantity.divide(
//                        mQuantityAutoConversionByUnitFactor,
//                        1,
//                        java.math.RoundingMode.HALF_DOWN
//                    ).toLong()
//                )
//            } catch (ignored: java.lang.Exception) {
//            }
//
//            return summary
//        }
//
//    val quantitySummaryRemainder: Int?
//        /**
//         * 数量をまとめ数に換算した余りを取得する。単位による自動換算が有効でない場合はnullを返す
//         */
//        get() {
//            if (!isQuantityAutoConversionByUnit) {
//                return null
//            }
//
//            var remainder: Int? = null
//            try {
//                remainder = mQuantity.remainder(mQuantityAutoConversionByUnitFactor)
//            } catch (ignored: java.lang.Exception) {
//            }
//
//            return remainder
//        }
//
//    /**
//     * まとめ数から物品の数量を取得する。単位による自動換算が有効でない場合はnullを返す。
//     * まとめ係数がnullの時もnullを返す。
//     */
//    fun getQuantityFromSummaryUnit(quantitySummary: Int): Int? {
//        if (!isQuantityAutoConversionByUnit) {
//            return null
//        }
//
//        if (mQuantityAutoConversionByUnitFactor == null) {
//            return null
//        }
//
//        return quantitySummary.multiply(mQuantityAutoConversionByUnitFactor)
//    }
//
//    /**
//     * 数量をまとめ数に換算した数量に余りがあればtrueを返却
//     */
//    fun isQuantitySummaryRemainder(): Boolean {
//        val remainder: Int? = quantitySummaryRemainder
//        if (remainder == null || !isAutoConversionUnitEnabled) {
//            return false
//        }
//
//        return remainder != Int.ZERO
//    }
//
//    /**
//     * 引数で取得した数量をまとめ数に換算した数量に余りがあればtrueを返却
//     */
//    fun isCalculateQuantitySummaryRemainder(quantity: Int): Boolean {
//        val remainder: Int? = calculateQuantitySummaryRemainder(quantity)
//        if (remainder == null || !isAutoConversionUnitEnabled) {
//            return false
//        }
//
//        return remainder != Int.ZERO
//    }
//
//    val isAutoConversionUnitEnabled: Boolean
//        get() {
//            var accountManager: AccountManager =
//                AccountManager(InventoryManagerApplication.applicationContext)
//            if (accountManagerForTest != null) {
//                accountManager = accountManagerForTest
//            }
//
//            return ((quantityAutoConversionByUnitName != null) &&
//                    isQuantityAutoConversionByUnit &&
//                    accountManager.canUseFunction(AccountManager.AvailableFunction.UNIT_AUTO_CONVERSION))
//        }
//
//    var optimalInventoryLevel: Int?
//        get() = mOptimalInventoryLevel
//        set(optimalInventoryLevel) {
//            mOptimalInventoryLevel = MiscUtils.parseQuantity(optimalInventoryLevel)
//        }
//
//    companion object {
//        /**
//         * デフォルトのコードの接頭語.
//         */
//        private val DEFAULT_CODE_PREFIX = "tw"
//
//        /**
//         * プレミアム会員の最大登録可能物品数.負の値なので、無限大.
//         */
//        private val MAX_IN_COUNT_OF_STOCKS_FOR_PREMIUM = -1
//
//        /**
//         * 一般会員の最大登録可能物品数.
//         */
//        private val MAX_IN_COUNT_OF_STOCKS = 200
//
//        /**
//         * 登録可能数制限前のユーザーのの最大登録可能物品数.制限なし（-1）。
//         */
//        private val MAX_IN_COUNT_OF_STOCKS_FOR_EARLY_USERS = -1
//
//        /**
//         * 写真を保存する一時ファイル名のプレフィクス.
//         */
//        private val TEMP_PHOTO_FILE_NAME_PREFIX = "tempPhoto"
//
//        /**
//         * 写真のダウンロードの２重実行を防ぐためのフラグ.
//         */
//        private var isDownloading = false
//
//        @VisibleForTesting
//        fun createStockForTest(): Stock {
//            return Stock()
//        }
//
//        @VisibleForTesting
//        fun createStockWithIdForTest(id: Int): Stock {
//            val stock = Stock()
//            stock.id = id.toLong()
//            return stock
//        }
//
//        @VisibleForTesting
//        fun createStockWithQuantityAutoConversion(
//            isQuantityAutoConversionByUnit: Boolean,
//            quantityAutoConversionByUnitName: String?,
//            quantityAutoConversionByUnitFactor: Int?,
//            mockAccountManager: AccountManager?
//        ): Stock {
//            val stock = Stock()
//            stock.isQuantityAutoConversionByUnit = isQuantityAutoConversionByUnit
//            stock.quantityAutoConversionByUnitName = quantityAutoConversionByUnitName
//            stock.mQuantityAutoConversionByUnitFactor = quantityAutoConversionByUnitFactor
//            stock.accountManagerForTest = mockAccountManager
//            return stock
//        }
//
//        /**
//         * 物品についてサーバーとローカルDBを同期する.
//         *
//         * @param context
//         * @param incremental      差分同期か.
//         * @param syncFailureInfos
//         * @return
//         * @throws FileNotFoundException
//         * @throws IOException
//         * @throws JSONException
//         * @throws ParseException
//         * @throws LocalDatabaseException
//         * @throws ZaicoHttpException
//         * @throws NoValidAccountException
//         * @throws UnavailableUserException
//         */
//        @Throws(
//            java.io.IOException::class,
//            java.lang.InstantiationException::class,
//            java.lang.IllegalAccessException::class,
//            JSONException::class,
//            java.text.ParseException::class,
//            LocalDatabaseException::class,
//            ZaicoHttpException::class,
//            NoValidAccountException::class,
//            UnavailableUserException::class
//        )
//        fun sync(
//            context: android.content.Context,
//            incremental: Boolean,
//            syncFailureInfos: java.util.ArrayList<InventoryObjectSyncFailureInfo>
//        ) {
//            val start: java.util.Calendar = java.util.Calendar.getInstance()
//
//            var lastSyncTime: Long
//            if (incremental && SyncInfoDao.hasSync(context)) {
//                lastSyncTime = SyncInfoDao.getLastStockSyncServerTime(context)
//            } else {
//                lastSyncTime = -1
//            }
//
//            // 在庫一覧刷新対応後、一度だけ全同期する必要があるため、全同期したか確認
//            if (!SyncInfoDao.hasSyncAfterInventoryListRenewal(context)) {
//                lastSyncTime = -1
//                SyncInfoDao.saveSyncAfterInventoryListRenewal(context)
//            }
//
//            val syncServerTime = downloadAndSync(context, lastSyncTime, syncFailureInfos)
//
//            syncUploadOnly(context, syncFailureInfos)
//
//            if (syncFailureInfos.isEmpty()) {
//                SyncInfoDao
//                    .saveLastStockSyncServerTimeAfterPagination(context, syncServerTime)
//
//                val elapseTime: Long =
//                    java.util.Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()
//                val params: android.os.Bundle = android.os.Bundle()
//                params.putLong(FirebaseAnalytics.Param.VALUE, elapseTime)
//                (context.getApplicationContext() as InventoryManagerApplication).sendEventToAnalytics(
//                    "millis_during_sync_stock",
//                    params
//                )
//            }
//        }
//
//        /**
//         * すべての在庫の同期状態をinsertにする.
//         *
//         * @param context
//         */
//        private fun setAllStocksSyncStateInsert(context: android.content.Context) {
//            val values: ContentValues = ContentValues(1)
//            values.put(DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE, DatabaseContract.Stock.INSERT)
//            context.getContentResolver()
//                .update(DatabaseContract.Stock.CONTENT_URI, values, null, null)
//        }
//
//        /**
//         * サーバーから在庫情報を取得してローカルdbに反映させる.
//         *
//         * @param context
//         * @param lastSyncTime     前回在庫取得時刻.サーバーの時計に依る.-1の場合は過去すべてを取得.
//         * @param syncFailureInfos
//         * @return 在庫取得時刻.サーバーの時計に依る.
//         * @throws UnsupportedEncodingException
//         * @throws MalformedURLException
//         * @throws ProtocolException
//         * @throws JSONException
//         * @throws ParseException
//         * @throws IOException
//         * @throws ZaicoHttpException
//         */
//        @Throws(
//            java.lang.InstantiationException::class,
//            java.lang.IllegalAccessException::class,
//            JSONException::class,
//            java.text.ParseException::class,
//            java.io.IOException::class,
//            ZaicoHttpException::class
//        )
//        private fun downloadAndSync(
//            context: android.content.Context, lastSyncTime: Long,
//            syncFailureInfos: java.util.ArrayList<InventoryObjectSyncFailureInfo>
//        ): Long {
//            val serverStocks: java.util.ArrayList<Stock> = java.util.ArrayList<Stock>()
//            var syncProgress: com.tamurasouko.twics.inventorymanager.model.Stock.SyncTimeAndSinceId =
//                com.tamurasouko.twics.inventorymanager.model.Stock.SyncTimeAndSinceId(-1, 0)
//
//            val isDoingBulkInsert = !existStock(context, true)
//
//            while (syncProgress.sinceId >= 0) {
//                serverStocks.clear()
//                syncProgress = getFromServer(
//                    context,
//                    isDoingBulkInsert,
//                    serverStocks,
//                    lastSyncTime,
//                    syncProgress.sinceId
//                )
//
//                if (isDoingBulkInsert) {
//                    if (serverStocks.size > 0) {
//                        bulkFirstInsertStocksFromServer(context, serverStocks)
//                    }
//                } else {
//                    for (stock: Stock in serverStocks) {
//                        updateOrInsertStocksFromServer(context, stock, syncFailureInfos)
//                    }
//                }
//            }
//
//            return syncProgress.syncTime
//        }
//
//        /**
//         * バルクinsert用の形式に一括変換する
//         * 削除された在庫は無視する
//         *
//         * @param stocks
//         * @return
//         */
//        private fun getPropertiesForLocalDB(stocks: java.util.ArrayList<Stock>): Array<ContentValues> {
//            val values: java.util.ArrayList<ContentValues> =
//                java.util.ArrayList<ContentValues>(stocks.size)
//
//            for (stock: Stock in stocks) {
//                if (!stock.isDeleted()) {
//                    values.add(stock.propertyForLocalDb)
//                }
//            }
//
//            return values.toTypedArray<ContentValues>()
//        }
//
//        /**
//         * サーバーから在庫情報を取得してserverStocks変数に保存する。
//         * serverStocks変数はreturnしないが、参照渡しなので関数実行後は値が格納されている。
//         *
//         * @param context
//         * @param notDeletedOnly 削除されていない在庫のみ取得する。在庫データをAndroid側で持っていない場合にtrueを渡す。
//         * @param serverStocks   取得した在庫を保存する.
//         * @param lastSyncTime   前回取得した時刻.サーバーの時計に依る.-1なら過去すべてを取得.
//         * @param sinceId        同期を始めるinventoryのサーバ側id。
//         * @return 取得したページが最終ページなら取得した時刻.最終ページ以外なら-1.
//         * @throws JSONException
//         * @throws ParseException
//         * @throws UnsupportedEncodingException
//         * @throws MalformedURLException
//         * @throws ProtocolException
//         * @throws IOException
//         */
//        @Throws(
//            java.lang.InstantiationException::class,
//            java.lang.IllegalAccessException::class,
//            JSONException::class,
//            java.text.ParseException::class,
//            java.io.IOException::class
//        )
//        private fun getFromServer(
//            context: android.content.Context,
//            notDeletedOnly: Boolean,
//            serverStocks: java.util.ArrayList<Stock>,
//            lastSyncTime: Long,
//            sinceId: Int
//        ): com.tamurasouko.twics.inventorymanager.model.Stock.SyncTimeAndSinceId {
//            var lastSyncTimeParam: String? = null
//            if (lastSyncTime > 0) {
//                lastSyncTimeParam = MiscUtils.formatIso8601ExtendedFormat(lastSyncTime)
//            }
//
//            var notDeletedParam: String? = null
//            if (notDeletedOnly) {
//                notDeletedParam = "true"
//            }
//
//            val client: ApiClient = ApiClientManager.apiClient()
//            val call: Call<ResponseBody> =
//                client.getInventoriesSince(sinceId, lastSyncTimeParam, notDeletedParam)
//            val response: Response<ResponseBody> = call.execute()
//
//            throwIfApiCallFailed(response)
//
//            // string()によりResponseBodyはClose
//            val jsonObject: org.json.JSONObject = org.json.JSONObject(response.body().string())
//            val objectsInJson: org.json.JSONArray = jsonObject.getJSONArray("inventories")
//            val nextSinceId =
//                if (jsonObject.isNull("since_id")) -1 else jsonObject.getInt("since_id")
//
//            serverStocks.ensureCapacity(objectsInJson.length())
//            for (i in 0 until objectsInJson.length()) {
//                val `object`: org.json.JSONObject = objectsInJson.getJSONObject(i)
//                serverStocks.add(toInventoryObject(`object`, Stock::class.java) as Stock?)
//            }
//
//            if (nextSinceId < 0) {
//                return com.tamurasouko.twics.inventorymanager.model.Stock.SyncTimeAndSinceId(
//                    MiscUtils.parseIso8601ExtendedFormat(jsonObject.getString("sync_time")),
//                    -1
//                )
//            } else {
//                return com.tamurasouko.twics.inventorymanager.model.Stock.SyncTimeAndSinceId(
//                    -1L,
//                    nextSinceId
//                )
//            }
//        }
//
//        /**
//         * サーバーから取得した物品をローカルのDBに反映させる.
//         *
//         * @param context
//         * @param stock            取得した物品.
//         * @param syncFailureInfos
//         */
//        private fun updateOrInsertStocksFromServer(
//            context: android.content.Context, stock: Stock,
//            syncFailureInfos: java.util.ArrayList<InventoryObjectSyncFailureInfo>
//        ) {
//            val localStock = get(context, stock.getCommonId())
//            try {
//                if (localStock == null) {
//                    if (!stock.insertFromServer(context)) {
//                        throw java.lang.RuntimeException("サーバで新たに作成した在庫の挿入に失敗しました.")
//                    }
//                } else {
//                    // ローカルに記録された前回サーバ同期時刻よりサーバ側の在庫の更新時刻が新しかったらコンフリクト発生
//                    if ((localStock.mSyncState === DatabaseContract.Stock.UPDATE) && (localStock.mUpdatedAtWhenSync != null) && (localStock.mUpdatedAtWhenSync!! > 0) && (localStock.mUpdatedAtWhenSync < stock.getUpdatedAt())) {
//                        FirebaseCrashlytics.getInstance()
//                            .setCustomKey("Conflict common_id", localStock.getCommonId())
//                        FirebaseCrashlytics.getInstance()
//                            .recordException(java.lang.RuntimeException("コンフリクト発生"))
//                    }
//
//                    val diffInUpdateAt: Long =
//                        stock.compareUpdateAtTo(localStock)
//                    if (localStock.inventoryId == 0) {
//                        // 在庫刷新後ローカルでもinventoryIdを持つようになったため、保持していない場合は更新する
//                        if (!stock.updateFromServer(context)) {
//                            throw java.lang.RuntimeException("サーバで更新された在庫の更新に失敗しました.")
//                        }
//                    } else if (diffInUpdateAt == 0L) {
//                        if (localStock.mSyncState !== NOTHING) {
//                            localStock.setSyncStateNothingAndResetUpdatedAtWhenSync(context)
//                            FirebaseCrashlytics.getInstance()
//                                .recordException(java.lang.RuntimeException("偶然ローカルとサーバで同時刻に変更された"))
//                        }
//                    } else if (diffInUpdateAt > 0) {
//                        if (!stock.updateFromServer(context)) {
//                            throw java.lang.RuntimeException("サーバで更新された在庫の更新に失敗しました.")
//                        }
//                    } else {
//                        localStock.setSyncStateUpdate(context)
//                    }
//                }
//            } catch (e: java.io.IOException) {
//                storeSyncFailure(stock, e, syncFailureInfos, false)
//            }
//        }
//
//        /**
//         * サーバーから取得した物品をローカルのDBにバルクでinsertする.
//         * 初めて同期する際のみ利用可能。
//         * 画像もダウンロードする。
//         * 画像が一つでもダウンロードできなかった場合でも成功とみなされる.
//         *
//         * @param context
//         * @param serverStocks
//         */
//        private fun bulkFirstInsertStocksFromServer(
//            context: android.content.Context,
//            serverStocks: java.util.ArrayList<Stock>
//        ) {
//            InventoryObject.bulkInsertFromServer(
//                context,
//                bulkInsertContentUri, getPropertiesForLocalDB(serverStocks)
//            )
//        }
//
//        /**
//         * ダウンロードされていない写真をダウンロードする
//         *
//         * @param context
//         */
//        fun downloadNotDownloadedPhotos(context: android.content.Context) {
//            if (isDownloading) {
//                return
//            }
//            isDownloading = true
//            try {
//                val stocks: java.util.ArrayList<Stock>? = getHavingNotDownloadedPhotoStocks(context)
//                val accountManager: AccountManager = AccountManager(context)
//                if (stocks != null) {
//                    for (stock: Stock in stocks) {
//                        if (!MiscUtils.isNetworkConnected(context) || !accountManager.isLogin()) {
//                            return
//                        }
//                        try {
//                            // ダウンロードを開始してから内容が変わっている場合を想定して、リロードする
//                            val reloaded = stock.reload(context)
//                            reloaded?.downloadPhotoAsynchronous(context)
//                        } catch (e: java.io.IOException) {
//                            // ネットワークエラーなど一般的なエラーは無視
//                        }
//                    }
//                }
//            } catch (e: java.lang.Exception) {
//                FirebaseCrashlytics.getInstance().recordException(e)
//            } finally {
//                isDownloading = false
//            }
//        }
//
//        private fun getHavingNotDownloadedPhotoStocks(context: android.content.Context): java.util.ArrayList<Stock>? {
//            val selection: String =
//                ((DatabaseContract.Stock.COLUMN_NAME_PHOTO_FILE_NAME + " LIKE 'http%' AND "
//                        + DatabaseContract.Stock.COLUMN_NAME_DEL_FLG).toString() + "="
//                        + DatabaseContract.Stock.FALSE)
//
//            var cursor: android.database.Cursor? = null
//            val stocks: java.util.ArrayList<Stock>
//            try {
//                cursor = context.getContentResolver().query(
//                    DatabaseContract.Stock.CONTENT_URI,
//                    null, selection,
//                    null, null
//                )
//
//                if (cursor == null || cursor.getCount() <= 0) {
//                    return null
//                }
//
//                stocks = java.util.ArrayList()
//                while (cursor.moveToNext()) {
//                    stocks.add(Stock().toObject(cursor))
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close()
//                }
//            }
//
//            return stocks
//        }
//
//        private val bulkInsertContentUri: android.net.Uri
//            get() = DatabaseContract.Stock.CONTENT_URI
//
//        /**
//         * ローカルdbのサーバーへアップロードすべき在庫をアップロードする.
//         * DEV-3143 リクエストが多くなりすぎない様、データ 50 件につき 10 秒の sleep を入れる
//         *
//         * @param context
//         * @param syncFailureInfos
//         * @throws InsufficientPrivilegeException
//         */
//        private fun uploadStocksToServer(
//            context: android.content.Context,
//            syncFailureInfos: java.util.ArrayList<InventoryObjectSyncFailureInfo>
//        ) {
//            val ids: java.util.ArrayList<Int>? = getIdsOfStocksToBeUploaded(context)
//                ?: return
//
//            val authority: Boolean = AccountManager(context).hasAuthorityToEdit()
//
//            var sleepCounter = 0
//
//            for (id: Int in ids) {
//                sleepCounter++
//                if (sleepCounter % 50 == 0) {
//                    try {
//                        java.lang.Thread.sleep(10000) //
//                    } catch (e: java.lang.InterruptedException) {
//                        e.printStackTrace()
//                    }
//                }
//
//                val stock = getStockToUpload(context, id) ?: continue
//
//                if (!authority) {
//                    storeSyncFailure(
//                        stock, InsufficientPrivilegeException("在庫をアップロードしようとした"),
//                        syncFailureInfos, true
//                    )
//                    continue
//                }
//
//                try {
//                    stock.uploadStockToServer(context)
//                } catch (e: java.io.IOException) {
//                    storeSyncFailure(stock, e, syncFailureInfos, true)
//                } catch (e: JSONException) {
//                    storeSyncFailure(stock, e, syncFailureInfos, true)
//                } catch (e: java.lang.IllegalAccessException) {
//                    storeSyncFailure(stock, e, syncFailureInfos, true)
//                } catch (e: java.text.ParseException) {
//                    storeSyncFailure(stock, e, syncFailureInfos, true)
//                } catch (e: java.lang.InstantiationException) {
//                    storeSyncFailure(stock, e, syncFailureInfos, true)
//                }
//            }
//        }
//
//        /**
//         * ローカルdbにおけるサーバーへアップロードしなければならない在庫のidを取得.
//         *
//         * @param context
//         * @return
//         */
//        private fun getIdsOfStocksToBeUploaded(context: android.content.Context): java.util.ArrayList<Int>? {
//            var cursor: android.database.Cursor? = null
//            val ids: java.util.ArrayList
//            try {
//                cursor = context.getContentResolver().query(
//                    DatabaseContract.Stock.CONTENT_URI,
//                    arrayOf<String>(
//                        DatabaseContract.Stock._ID
//                    ), ((((DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE
//                            + "=" + DatabaseContract.Stock.INSERT).toString() + " OR "
//                            + DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE
//                            ).toString() + "=" + DatabaseContract.Stock.UPDATE)),
//                    null, DatabaseContract.Stock._ID
//                )
//
//                if (cursor == null || cursor.getCount() <= 0) {
//                    return null
//                }
//
//                ids = java.util.ArrayList(cursor.getCount())
//                val columnIndex: Int = cursor.getColumnIndexOrThrow(DatabaseContract.Stock._ID)
//                while (cursor.moveToNext()) {
//                    if (!cursor.isNull(columnIndex)) {
//                        ids.add(cursor.getInt(columnIndex))
//                    }
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close()
//                }
//            }
//
//            return ids
//        }
//
//        /**
//         * ローカルdbにあるサーバーにアップロードすべき在庫を一つ取得する.
//         *
//         * @param context
//         * @param id      在庫モデルのid
//         * @return
//         */
//        private fun getStockToUpload(context: android.content.Context, id: Int): Stock? {
//            var stock: Stock? = null
//            var cursor: android.database.Cursor? = null
//
//            try {
//                cursor = context.getContentResolver().query(
//                    DatabaseContract.Stock.CONTENT_URI,
//                    null, (((((DatabaseContract.Stock._ID + "=?"
//                            + " AND (" + DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE).toString() + "=" + DatabaseContract.Stock.INSERT
//                            ).toString() + " OR " + DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE).toString() + "=" + DatabaseContract.Stock.UPDATE).toString() + ")"),
//                    arrayOf<String>(id.toString()), null
//                )
//
//                if (cursor != null && cursor.getCount() == 1) {
//                    cursor.moveToFirst()
//                    stock = Stock().toObject(cursor)
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close()
//                }
//            }
//            return stock
//        }
//
//        /**
//         * 物品の同期中に同期に失敗した物品について情報を保存.
//         *
//         * @param stock
//         * @param e
//         * @param syncFailureInfos
//         * @param isUpload
//         */
//        private fun storeSyncFailure(
//            stock: Stock, e: java.lang.Exception,
//            syncFailureInfos: java.util.ArrayList<InventoryObjectSyncFailureInfo>, isUpload: Boolean
//        ) {
//            FirebaseCrashlytics.getInstance().recordException(e)
//            val info: InventoryObjectSyncFailureInfo = InventoryObjectSyncFailureInfo()
//            info.isUpload = isUpload
//            info.`object` = stock
//            syncFailureInfos.add(info)
//        }
//
//        /**
//         * 新しい物品を作成する.
//         *
//         * @param context コンテキスト
//         * @return 新しい物品.
//         */
//        fun create(context: android.content.Context): Stock {
//            return Stock(context)
//        }
//
//        /**
//         * 既存の物品を取得する.削除フラグおよびグループの所属に関わらず取得する.
//         *
//         * @param context  コンテキスト
//         * @param commonId 共通ID
//         * @return 物品.共通IDを持つ物品が見つからない場合、null.
//         */
//        fun get(context: android.content.Context, commonId: String?): Stock? {
//            // どういうシチュエーションか不明だけれど、commonIdがnullでクラッシュしていることがあるので、このチェックを追加する
//            if (commonId == null) {
//                return null
//            }
//
//            var stock: Stock? = null
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI, null,
//                DatabaseContract.Stock.COLUMN_NAME_COMMON_ID + "=?",
//                arrayOf<String>(commonId),
//                DatabaseContract.Stock._ID + " asc limit 1"
//            )
//            if ((null != cursor) && (cursor.getCount() == 1
//                        ) && cursor.moveToFirst()
//            ) {
//                stock = Stock().toObject(cursor)
//            }
//
//            if (cursor != null) {
//                cursor.close()
//            }
//
//            return stock
//        }
//
//        /**
//         * ローカルDBのROW idから物品を取得する.
//         *
//         * @param context
//         * @param rowId
//         * @return
//         */
//        fun getByRowId(context: android.content.Context, rowId: Long): Stock? {
//            var stock: Stock? = null
//            val uri: android.net.Uri =
//                ContentUris.withAppendedId(DatabaseContract.Stock.CONTENT_ID_URI_BASE, rowId)
//            val cursor: android.database.Cursor =
//                context.getContentResolver().query(uri, null, null, null, null)
//            if ((null != cursor) && (cursor.getCount() == 1
//                        ) && cursor.moveToFirst()
//            ) {
//                stock = Stock().toObject(cursor)
//            }
//
//            if (cursor != null) {
//                cursor.close()
//            }
//
//            return stock
//        }
//
//        /**
//         * commonIdを元に物品を取得する.
//         *
//         * @param context
//         * @param commonId
//         * @return stock
//         */
//        fun getByCommonId(context: android.content.Context, commonId: String): Stock? {
//            var cursor: android.database.Cursor? = null
//            try {
//                cursor = context.getContentResolver().query(
//                    DatabaseContract.Stock.CONTENT_URI,
//                    null,
//                    ((DatabaseContract.Stock.COLUMN_NAME_COMMON_ID + "=?"
//                            + " AND " + DatabaseContract.Stock.COLUMN_NAME_DEL_FLG).toString() + "=?"),
//                    arrayOf<String>(commonId, DatabaseContract.Stock.FALSE),
//                    DatabaseContract.Stock._ID + " asc limit 1"
//                )
//                if (cursor != null && cursor.moveToFirst()) {
//                    return Stock().toObject(cursor)
//                }
//                return null
//            } finally {
//                if (cursor != null) {
//                    cursor.close()
//                }
//            }
//        }
//
//        /**
//         * inventoryIdを元に物品を取得する.
//         *
//         * @param context
//         * @param inventoryId
//         * @return stock
//         */
//        fun getByInventoryId(context: android.content.Context, inventoryId: Int): Stock? {
//            var cursor: android.database.Cursor? = null
//
//            if (inventoryId == 0) {
//                return null
//            }
//
//            try {
//                val selection: java.lang.StringBuilder =
//                    java.lang.StringBuilder(DatabaseContract.Stock.COLUMN_NAME_DEL_FLG + "=" + DatabaseContract.Stock.FALSE)
//                val selectionArgs: java.util.ArrayList<String> = java.util.ArrayList<String>()
//
//                selection.append((" AND " + DatabaseContract.Stock.COLUMN_NAME_INVENTORY_ID).toString() + "=?")
//                selectionArgs.add(inventoryId.toString())
//
//                val userGroups: Array<String> = AccountManager(context).getUserGroups()
//
//                selection.append(" AND (")
//                for (i in userGroups.indices) {
//                    if (i > 0) {
//                        selection.append(" OR ")
//                    }
//                    selection.append(("','||" + DatabaseContract.Stock.COLUMN_NAME_USER_GROUP).toString() + "||',' LIKE ?")
//                    selectionArgs.add("%," + userGroups[i] + ",%")
//                }
//                selection.append(")")
//
//                cursor = context.getContentResolver().query(
//                    DatabaseContract.Stock.CONTENT_URI,
//                    null,
//                    selection.toString(),
//                    selectionArgs.toTypedArray<String>(),
//                    DatabaseContract.Stock._ID + " asc limit 1"
//                )
//
//                if (cursor != null && cursor.moveToFirst()) {
//                    return Stock().toObject(cursor)
//                }
//                return null
//            } finally {
//                if (cursor != null) {
//                    cursor.close()
//                }
//            }
//        }
//
//        /**
//         * Titleの値で在庫を検索.複数見つかった場合は複数返す.
//         *
//         * @param context
//         * @param title
//         * @return 見つからない場合、null.
//         */
//        fun findMultiByTitle(
//            context: android.content.Context,
//            title: String?
//        ): java.util.ArrayList<Stock> {
//            var cursor: android.database.Cursor? = null
//            val selectionArgs: java.util.ArrayList<String> = java.util.ArrayList<String>()
//            val selection: java.lang.StringBuilder = java.lang.StringBuilder()
//
//            selection
//                .append(DatabaseContract.Stock.COLUMN_NAME_TITLE + "=? AND ")
//                .append(DatabaseContract.Stock.COLUMN_NAME_DEL_FLG + "=" + DatabaseContract.Stock.FALSE)
//            selectionArgs.add(title)
//
//            addUserGroupSelection(context, selection, selectionArgs)
//
//            val sortOrder: String = DatabaseContract.Stock._ID + " ASC"
//
//            cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                null,
//                selection.toString(),
//                selectionArgs.toTypedArray<String>(),
//                sortOrder
//            )
//
//            return getStocksFromCursor(cursor)
//        }
//
//        private fun addUserGroupSelection(
//            context: android.content.Context,
//            selection: java.lang.StringBuilder,
//            selectionArgs: java.util.ArrayList<String>
//        ) {
//            val userGroups: Array<String> = AccountManager(context).getUserGroups()
//            selection.append(" AND (")
//            for (i in userGroups.indices) {
//                if (i > 0) {
//                    selection.append(" OR ")
//                }
//                selection.append(("','||" + DatabaseContract.Stock.COLUMN_NAME_USER_GROUP).toString() + "||',' LIKE ?")
//                selectionArgs.add("%," + userGroups[i] + ",%")
//            }
//            selection.append(")")
//        }
//
//        private fun getStocksFromCursor(cursor: android.database.Cursor?): java.util.ArrayList<Stock> {
//            val stocks: java.util.ArrayList<Stock> = java.util.ArrayList<Stock>()
//            if (cursor == null || cursor.getCount() <= 0) {
//                return stocks
//            }
//            try {
//                while (cursor.moveToNext()) {
//                    stocks.add(Stock().toObject(cursor))
//                }
//            } finally {
//                cursor.close()
//            }
//            return stocks
//        }
//
//        /**
//         * コードの値で物品を検索.複数見つかった場合は複数返す.
//         *
//         * @param context
//         * @param code
//         * @return 見つからない場合、null.
//         */
//        fun findMultiByCode(
//            context: android.content.Context,
//            code: String
//        ): java.util.ArrayList<Stock> {
//            var cursor: android.database.Cursor? = null
//            val stocks: java.util.ArrayList<Stock> = java.util.ArrayList()
//
//            try {
//                cursor = getCursorToFindMultiByCode(context, code)
//
//                if (cursor == null || cursor.getCount() <= 0) {
//                    return stocks
//                }
//
//                while (cursor.moveToNext()) {
//                    stocks.add(Stock().toObject(cursor))
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close()
//                }
//            }
//
//            return stocks
//        }
//
//        /**
//         * ZAICON IDの値で在庫を検索.複数見つかった場合は複数返す.
//         *
//         * @param context
//         * @param code
//         * @return 見つからない場合、null.
//         */
//        fun findMultiByZaiconId(
//            context: android.content.Context,
//            code: String
//        ): java.util.ArrayList<Stock> {
//            var cursor: android.database.Cursor? = null
//            val stocks: java.util.ArrayList<Stock> = java.util.ArrayList()
//
//            try {
//                cursor = getCursorToFindMultiByZaiconId(context, code)
//
//                if (cursor == null || cursor.getCount() <= 0) {
//                    return stocks
//                }
//
//                while (cursor.moveToNext()) {
//                    stocks.add(Stock().toObject(cursor))
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close()
//                }
//            }
//
//            return stocks
//        }
//
//        /**
//         * コードの値で物品を検索.カーソルを返す.
//         *
//         * @param context
//         * @param code
//         * @return
//         */
//        fun getCursorToFindMultiByCode(
//            context: android.content.Context,
//            code: String
//        ): android.database.Cursor {
//            val codeReplaced = code.replace(java.lang.System.getProperty("line.separator"), "")
//
//            var selection: String =
//                ((DatabaseContract.Stock.COLUMN_NAME_CODE + "=? AND "
//                        + DatabaseContract.Stock.COLUMN_NAME_DEL_FLG).toString() + "="
//                        + DatabaseContract.Stock.FALSE)
//            val selectionArgs: java.util.ArrayList<String> = java.util.ArrayList<String>()
//            selectionArgs.add(codeReplaced)
//
//            val userGroups: Array<String> = AccountManager(context).getUserGroups()
//
//            selection += " AND ("
//            for (i in userGroups.indices) {
//                if (i > 0) {
//                    selection += " OR "
//                }
//                selection += ("','||" + DatabaseContract.Stock.COLUMN_NAME_USER_GROUP).toString() + "||',' LIKE ?"
//                selectionArgs.add("%," + userGroups[i] + ",%")
//            }
//            selection += ")"
//
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                null, selection,
//                selectionArgs.toTypedArray<String>(),
//                DatabaseContract.Stock._ID + " ASC"
//            )
//
//            return cursor
//        }
//
//        /**
//         * ローカルのDBから追加項目「ZAICON ID」をcodeで検索した結果のカーソルを返す
//         *
//         * @param context
//         * @param code
//         * @return
//         */
//        fun getCursorToFindMultiByZaiconId(
//            context: android.content.Context,
//            code: String
//        ): android.database.Cursor {
//            val codeReplaced = code.replace(java.lang.System.getProperty("line.separator"), "")
//            val searchWords =
//                ("ZAICON ID%" + OptionalAttribute.SEPARATOR_ATTRIBUTE_TITLE_AND_CONTENT).toString() + codeReplaced
//            var selection: String = ((((((((((("( "
//                    + DatabaseContract.Stock.COLUMN_NAME_OPTIONAL_ATTRIBUTES).toString() + " LIKE '" + searchWords + "' OR "
//                    + DatabaseContract.Stock.COLUMN_NAME_OPTIONAL_ATTRIBUTES).toString() + " LIKE '" + searchWords + OptionalAttribute.SEPARATOR_ATTRIBUTE).toString() + "%' OR "
//                    + DatabaseContract.Stock.COLUMN_NAME_OPTIONAL_ATTRIBUTES).toString() + " LIKE '%" + OptionalAttribute.SEPARATOR_ATTRIBUTE).toString() + searchWords + "' OR "
//                    + DatabaseContract.Stock.COLUMN_NAME_OPTIONAL_ATTRIBUTES).toString() + " LIKE '%" + OptionalAttribute.SEPARATOR_ATTRIBUTE).toString() + searchWords + OptionalAttribute.SEPARATOR_ATTRIBUTE).toString() + "%'"
//                    + " ) AND "
//                    + DatabaseContract.Stock.COLUMN_NAME_DEL_FLG).toString() + "="
//                    + DatabaseContract.Stock.FALSE))
//            val selectionArgs: java.util.ArrayList<String> = java.util.ArrayList<String>()
//
//            val userGroups: Array<String> = AccountManager(context).getUserGroups()
//
//            selection += " AND ("
//            for (i in userGroups.indices) {
//                if (i > 0) {
//                    selection += " OR "
//                }
//                selection += (("','||" + DatabaseContract.Stock.COLUMN_NAME_USER_GROUP
//                        ).toString() + "||',' LIKE ?")
//                selectionArgs.add("%," + userGroups[i] + ",%")
//            }
//            selection += ")"
//
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                null, selection,
//                selectionArgs.toTypedArray<String>(),
//                DatabaseContract.Stock._ID + " ASC"
//            )
//
//            return cursor
//        }
//
//        /**
//         * 削除されたものも含め、物品が存在するか.
//         *
//         * @param context
//         * @return
//         */
//        fun everExistStock(context: android.content.Context): Boolean {
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                null, null, null, DatabaseContract.Stock._ID + " ASC LIMIT 1"
//            )
//            val exist: Boolean = cursor.getCount() > 0
//            cursor.close()
//            return exist
//        }
//
//        /**
//         * カテゴリごとの削除されていない在庫数を返す.
//         *
//         * @param context
//         * @return 在庫数.
//         */
//        fun countsByCategory(context: android.content.Context): Map<String, Int> {
//            val countsByCategory: java.util.HashMap<String, Int> = java.util.HashMap<String, Int>()
//
//            var selection: String = ((((DatabaseContract.Stock.COLUMN_NAME_DEL_FLG + "="
//                    + DatabaseContract.Stock.FALSE
//                    ).toString() + " AND " + DatabaseContract.Stock.COLUMN_NAME_CATEGORY).toString() + " IS NOT NULL"
//                    + " AND " + DatabaseContract.Stock.COLUMN_NAME_CATEGORY).toString() + " <> ''")
//            val selectionArgs: java.util.ArrayList<String> = java.util.ArrayList<String>()
//
//            val userGroups: Array<String> = AccountManager(context).getUserGroups()
//
//            selection += " AND ("
//            for (i in userGroups.indices) {
//                if (i > 0) {
//                    selection += " OR "
//                }
//                selection += (("','||" + DatabaseContract.Stock.COLUMN_NAME_USER_GROUP
//                        ).toString() + "||',' LIKE ?")
//                selectionArgs.add("%," + userGroups[i] + ",%")
//            }
//            selection += ")"
//
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                arrayOf<String>(
//                    DatabaseContract.Stock.COLUMN_NAME_CATEGORY,
//                ),
//                selection,
//                selectionArgs.toTypedArray<String>(), null
//            )
//            while (cursor.moveToNext()) {
//                val category: String = cursor.getString(
//                    cursor
//                        .getColumnIndexOrThrow(DatabaseContract.Stock.COLUMN_NAME_CATEGORY)
//                )
//                val categories: Array<String> = android.text.TextUtils.split(
//                    category,
//                    DatabaseContract.Category.SPLIT_CHARACTER_IN_TITLE
//                )
//                for (singleCategory: String? in categories) {
//                    if (countsByCategory.containsKey(singleCategory)) {
//                        val count: Int = countsByCategory.get(singleCategory)
//                        countsByCategory.put(singleCategory, count + 1)
//                    } else {
//                        countsByCategory.put(singleCategory, 1)
//                    }
//                }
//            }
//            cursor.close()
//            return countsByCategory
//        }
//
//        /**
//         * すべての物品の追加項目名を変更する.
//         *
//         * @param before 変更前の追加項目名
//         * @param after  変更後の追加項目名
//         * @context
//         */
//        fun substituteAttributeTitleForAll(
//            context: android.content.Context, before: String,
//            after: String
//        ) {
//            val extras: android.os.Bundle = android.os.Bundle()
//            extras.putString(
//                InventoryProvider.INVENTORY_ATTRIBUTE_TITLE_WITH_SEPARATER_BEFORE, (before
//                        + OptionalAttribute.SEPARATOR_ATTRIBUTE_TITLE_AND_CONTENT)
//            )
//            extras.putString(
//                InventoryProvider.INVENTORY_ATTRIBUTE_TITLE_WITH_SEPARATER_AFTER, (after
//                        + OptionalAttribute.SEPARATOR_ATTRIBUTE_TITLE_AND_CONTENT)
//            )
//            context.getContentResolver().call(
//                DatabaseContract.CONTENT_URI,
//                InventoryProvider.METHOD_REPLACE_INVENTORY_ATTRIBUTE_TITLE_IN_STOCKS, null, extras
//            )
//        }
//
//        /**
//         * 一つでも削除されていない在庫があるか.
//         *
//         * @param context
//         * @param includeDeleted
//         * @return
//         */
//        fun existStock(context: android.content.Context, includeDeleted: Boolean): Boolean {
//            var selection: String? = null
//            if (!includeDeleted) {
//                selection =
//                    DatabaseContract.Stock.COLUMN_NAME_DEL_FLG + "=" + DatabaseContract.Stock.FALSE
//            }
//
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                null, selection, null, DatabaseContract.Stock._ID + " ASC LIMIT 1"
//            )
//            val exist: Boolean = cursor.getCount() > 0
//            cursor.close()
//            return exist
//        }
//
//        /**
//         * サーバーに同期していない更新された物品が存在するか.直前に更新された物品は無視する.
//         *
//         * @param context
//         * @param threshholdTimeInSec この時間以内に更新された物品は無視する.
//         * @return
//         */
//        fun hasUpdatedOld(context: android.content.Context, threshholdTimeInSec: Int): Boolean {
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                arrayOf<String>(
//                    "count(*) as count",
//                ),
//                ((((DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE
//                        + "=" + DatabaseContract.Stock.UPDATE).toString() + " AND "
//                        + DatabaseContract.Stock.COLUMN_NAME_UPDATED_AT).toString() + " <= "
//                        + MiscUtils.getPastTimeInSec(threshholdTimeInSec))),
//                null, null
//            )
//
//            cursor.moveToFirst()
//            val count: Int = cursor.getInt(0)
//            cursor.close()
//            return count > 0
//        }
//
//        /**
//         * アップロードのみの同期を実行する.
//         *
//         * @param mContext
//         * @param syncFailureInfos
//         */
//        fun syncUploadOnly(
//            mContext: android.content.Context,
//            syncFailureInfos: java.util.ArrayList<InventoryObjectSyncFailureInfo>
//        ) {
//            uploadStocksToServer(mContext, syncFailureInfos)
//        }
//
//        /**
//         * アップロードすべき物品が存在するか.
//         *
//         * @param context
//         * @return
//         */
//        fun toUpload(context: android.content.Context): Boolean {
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                arrayOf<String>(
//                    "count(*) as count",
//                ),
//                (((((DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE + "=" + DatabaseContract.Stock.UPDATE
//                        ).toString() + " OR "
//                        + DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE).toString() + "="
//                        + DatabaseContract.Stock.INSERT
//                        ).toString() + " AND "
//                        + DatabaseContract.Stock.COLUMN_NAME_DEL_FLG).toString() + " = "
//                        + DatabaseContract.Stock.FALSE),
//                null, null
//            )
//
//            cursor.moveToFirst()
//            val count: Int = cursor.getInt(0)
//            cursor.close()
//            return count > 0
//        }
//
//        /**
//         * 在庫の数が最大値未満か.
//         *
//         * @return
//         */
//        fun isUnderLimitOnCountOfStocks(context: android.content.Context): Boolean {
//            if (limitOnCountOfStocks(context) < 0) {
//                return true
//            }
//
//            return count(context) < limitOnCountOfStocks(context)
//        }
//
//        /**
//         * 削除されていない物品の総数を取得する.物品の数量の総数ではない.
//         *
//         * @param context
//         * @return
//         */
//        fun count(context: android.content.Context): Int {
//            val cursor: android.database.Cursor = context.getContentResolver().query(
//                DatabaseContract.Stock.CONTENT_URI,
//                arrayOf<String>(
//                    "count(*) as count",
//                ), ((DatabaseContract.Stock.COLUMN_NAME_DEL_FLG
//                        + "=" + DatabaseContract.Stock.FALSE)),
//                null, DatabaseContract.Stock._ID
//            )
//
//            cursor.moveToFirst()
//            val count: Int = cursor.getInt(0)
//            cursor.close()
//            return count
//        }
//
//        /**
//         * 物品数の最大数.制限なしの場合、負の値。
//         *
//         * @param context
//         * @return
//         */
//        fun limitOnCountOfStocks(context: android.content.Context?): Int {
//            val accountManager: AccountManager = AccountManager(context)
//
//            if (!accountManager.isCreatedAfterLimitCountOfStocks()) {
//                return MAX_IN_COUNT_OF_STOCKS_FOR_EARLY_USERS
//            }
//
//            if (accountManager.canUseFunction(CreateUnlimitedNumberOfStocks)) {
//                return MAX_IN_COUNT_OF_STOCKS_FOR_PREMIUM
//            }
//
//            return MAX_IN_COUNT_OF_STOCKS
//        }
//
//        /**
//         * 2ヶ月以上前の削除された同期済みの在庫を削除
//         * 削除された在庫データでDBがいっぱいになるのを防ぐため。
//         */
//        fun deleteOldDeletedStocks(context: android.content.Context) {
//            val selectionClause: String =
//                (((((DatabaseContract.Stock.COLUMN_NAME_SYNC_STATE + "=" + NOTHING).toString() + " AND "
//                        + DatabaseContract.Stock.COLUMN_NAME_DEL_FLG).toString() + "=" + DatabaseContract.Stock.TRUE).toString() + " AND "
//                        + DatabaseContract.Stock.COLUMN_NAME_UPDATED_AT).toString() + "<?")
//            val selectionArgs =
//                arrayOf<String>(java.lang.String.valueOf(MiscUtils.getCurrentTimeInSec() - MiscUtils.TWO_MONTHS_IN_SEC))
//            context.getContentResolver()
//                .delete(DatabaseContract.Stock.CONTENT_URI, selectionClause, selectionArgs)
//        }
//
//
//        /**
//         * カーソルからStockを取得する
//         *
//         * @param cursor
//         * @return
//         */
//        fun getByCursor(cursor: android.database.Cursor?): Stock {
//            return Stock().toObject(cursor)
//        }
//
//        /**
//         * 既存在庫データからコピーする。codeはコピーしない。common_idなど画面から変更すべきでないものもコピーしない。
//         *
//         * @param context
//         * @param src
//         * @return
//         */
//        fun copy(context: android.content.Context, src: Stock): Stock {
//            val copied = create(context)
//            copied.title = src.title
//            copied.state = src.state
//            copied.place = src.place
//            if (src.orderPoint != null) {
//                copied.orderPoint = src.formatOrderPointForInput()
//            }
//            copied.setIsOrderPointWarningDisabled(src.mIsCheckedOrderPointWarningDisabled)
//            copied.etc = src.etc
//            copied.unit = src.unit
//            copied.category = src.category
//            copied.optionalAttribute = src.optionalAttribute
//            // 発注時適正在庫数。ただし、利用可能なプランの場合のみ。
//            if ((src.optimalInventoryLevel != null
//                        && InventoryUtilsKt.useOptimalInventoryLevel(context, null))
//            ) {
//                copied.optimalInventoryLevel =
//                    MiscUtils.formatQuantityWithoutGrouping(src.mOptimalInventoryLevel)
//            }
//            // ユーザーグループも。ただし、管理者権限のみ。
//            if (AccountManager(context).isAdmin()) {
//                copied.userGroup = src.userGroup
//            }
//            // 写真
//            // 写真が存在しなかったら写真はコピーしない
//            val srcPhotoFile: java.io.File? = src.getPhotoFile(context)
//            if (srcPhotoFile != null) {
//                // 写真だけ先に保存してしまう
//                val photoFileName = src.photoFileName
//                val file: java.io.File =
//                    FileConvention.getPhotoFilePath(context, copied.getCommonId(), photoFileName)
//                try {
//                    org.apache.commons.io.FileUtils.copyFile(srcPhotoFile, file)
//                    copied.photoFileName = photoFileName
//                } catch (e: java.io.IOException) {
//                    FirebaseCrashlytics.getInstance().recordException(e)
//                }
//            }
//
//            return copied
//        }
//
//        @Throws(java.io.IOException::class)
//        fun createTempPhotoFile(context: android.content.Context): java.io.File {
//            return java.io.File.createTempFile(
//                TEMP_PHOTO_FILE_NAME_PREFIX,
//                null,
//                context.getCacheDir()
//            )
//        }
//
//        /**
//         * スキャン共通の表示対象のカラム名一覧を返す。
//         *
//         * 下記のファイルの処理を参考にしている。
//         * app/src/main/java/com/tamurasouko/twics/inventorymanager/fragment/StockInfoForStocktakeDialog.java#onCreateView
//         *
//         * @param context コンテキスト
//         * @return カラム名の一覧、追加項目は先頭にアンダースコアが付与されている
//         */
//        fun getStockAttributeNamesForScan(context: android.content.Context?): List<String> {
//            val names: java.util.ArrayList<String> = java.util.ArrayList<String>()
//
//            // 上から順番に項目を表示する
//            if (UiSelectionDao.isDisplayPlaceOnStocktake(context)) {
//                names.add("place")
//            }
//            if (UiSelectionDao.isDisplayStateOnStocktake(context)) {
//                names.add("state")
//            }
//            if (UiSelectionDao.isDisplayCategoryOnStocktake(context)) {
//                names.add("category")
//            }
//            if (UiSelectionDao.isDisplayCodeOnStocktake(context)) {
//                names.add("code")
//            }
//            if (UiSelectionDao.isDisplayEtcOnStocktake(context)) {
//                names.add("etc")
//            }
//
//            // 追加項目
//            val attrs: java.util.ArrayList<String> =
//                UiSelectionDao.getDisplayInventoryAttributeOnStocktake(context)
//            if (!attrs.isEmpty()) {
//                for (title: String in attrs) {
//                    names.add("_$title")
//                }
//            }
//
//            return names
//        }
//    }
//}