package org.irislang.jiris.core

class IrisValue {
    var `object`: IrisObject? = null

    internal fun equals(value: IrisValue): Boolean {
        return `object` === value.`object`
    }

    companion object {

        @JvmStatic
        fun WrapObject(obj: IrisObject): IrisValue {
            val value = IrisValue()
            value.`object` = obj
            return value
        }

        @JvmStatic
        fun CloneValue(value: IrisValue): IrisValue {
            val v = IrisValue()
            v.`object` = value.`object`
            return v
        }
    }

}
