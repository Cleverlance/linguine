package com.qinshift.linguine.localisation.presentation

import util.Logger

internal interface Localisation {

    fun get(key: String, vararg args: String): String?

    object Empty : Localisation {
        override fun get(key: String, vararg args: String): String? = null
    }

    object KeyBased : Localisation {
        override fun get(key: String, vararg args: String) = key.also {
            Logger.warn("No localisation found for $key")
        }
    }

    data class MapBased(private val map: Map<String, String>) : Localisation {
        override fun get(key: String, vararg args: String): String? {
            return args.foldIndexed(map[key]) { index, acc, argument ->
                acc?.replace("%${index + 1}\$s", argument)
            }
        }
    }
}
