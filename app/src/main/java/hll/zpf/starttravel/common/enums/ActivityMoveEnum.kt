package hll.zpf.starttravel.common.enums

enum class ActivityMoveEnum(value:Int) {
    /**
     * 横向开始
     */
    START_FROM_RIGHT(0),
    /**
     * 横向返回
     */
    BACK_FROM_LEFT(1),
    /**
     * 纵向开始
     */
    START_FROM_BOTTOM(2),
    /**
     * 纵向返回
     */
    BACK_FROM_TOP(3)
}