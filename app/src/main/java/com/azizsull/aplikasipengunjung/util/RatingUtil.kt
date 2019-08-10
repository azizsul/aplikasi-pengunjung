package com.azizsull.aplikasipengunjung.util

import com.azizsull.aplikasipengunjung.model.FieldModel
import java.util.ArrayList
import java.util.UUID

/**
 * Utilities for Ratings.
 */
object RatingUtil {

    private val REVIEW_CONTENTS = arrayOf(
            // 0 - 1 stars
            "This was awful! Totally inedible.",

            // 1 - 2 stars
            "This was pretty bad, would not go back.",

            // 2 - 3 stars
            "I was fed, so that's something.",

            // 3 - 4 stars
            "This was a nice meal, I'd go back.",

            // 4 - 5 stars
            "This was fantastic!  Best ever!")

    /**
     * Create a random FieldModel POJO.
     */
    private val random: FieldModel
        get() {
            val rating = FieldModel()

            val score: String? = null

            rating.userId = UUID.randomUUID().toString()
            rating.fieldName = "Random User"
            rating.fieldType = score
            rating.price = 0

            return rating
        }

    /**
     * Get a list of random FieldModel POJOs.
     */
    fun getRandomList(length: Int): List<FieldModel> {
        val result = ArrayList<FieldModel>()

        for (i in 0 until length) {
            result.add(random)
        }

        return result
    }

    /**
     * Get the average fieldType of a List.
     */

}
