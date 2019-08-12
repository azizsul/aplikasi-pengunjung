package com.azizsull.aplikasipengunjung.util

import android.content.Context
import com.azizsull.aplikasipengunjung.R
import com.azizsull.aplikasipengunjung.model.PlaceModel
import java.util.Arrays
import java.util.Random

/**
 * Utilities for Restaurants.
 */
object PlaceUtil {

    private const val RESTAURANT_URL_FMT = "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png"
    private const val MAX_IMAGE_NUM = 22

    private val NAME_FIRST_WORDS = arrayOf(
            "Foo", "Bar", "Baz", "Qux", "Fire", "Sam's", "World Famous", "Google", "The Best")

    private val NAME_SECOND_WORDS = arrayOf(
            "PlaceModel", "Cafe", "Spot", "Eatin' Place", "Eatery", "Drive Thru", "Diner")

    /**
     * Create a random PlaceModel POJO.
     */
    fun getRandom(context: Context): PlaceModel {
        val places = PlaceModel()
        Random()

        // Cities (first elemnt is 'Any')
        var cities = context.resources.getStringArray(R.array.lokasi)
        cities = Arrays.copyOfRange(cities, 1, cities.size)

        // Categories (first element is 'Any')
        var categories = context.resources.getStringArray(R.array.categories)
        categories = Arrays.copyOfRange(categories, 1, categories.size)

        val prices = intArrayOf(1, 2, 3)

//        places.name = getRandomName(random)
//        places.alamat = getRandomString(cities, random)
//        places.category = getRandomString(categories, random)
////        places.images = getRandomImageUrl(random)
//        places.price = getRandomInt(prices, random)
//        places.numRatings = random.nextInt(20)

        // Note: average fieldType intentionally not set

        return places
    }

    /**
     * Get a random image.
     */
//    private fun getRandomImageUrl(random: Random): ArrayList<String> {
//        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
//        val id = random.nextInt(MAX_IMAGE_NUM) + 1
//
//        return String.format(Locale.getDefault(), RESTAURANT_URL_FMT, id)
//    }

    /**
     * Get price represented as dollar signs.
     */
//    fun getPriceString(placeModel: PlaceModel): String {
////        return getPriceString(placeModel.price)
//    }

    /**
     * Get price represented as dollar signs.
     */
    fun getPriceString(priceInt: Int): String {
        when (priceInt) {
            1 -> return "$"
            2 -> return "$$"
            3 -> return "$$$"
            else -> return "$$$"
        }
    }

    private fun getRandomName(random: Random): String {
        return (getRandomString(NAME_FIRST_WORDS, random) + " " +
                getRandomString(NAME_SECOND_WORDS, random))
    }

    private fun getRandomString(array: Array<String>, random: Random): String {
        val ind = random.nextInt(array.size)
        return array[ind]
    }

    private fun getRandomInt(array: IntArray, random: Random): Int {
        val ind = random.nextInt(array.size)
        return array[ind]
    }
}
