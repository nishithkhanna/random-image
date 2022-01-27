package com.nishithkhanna.randomimage

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.HttpException

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getPreferences(MODE_PRIVATE)

        val lastSeed = prefs.getString("seed", "")
        if(!lastSeed.isNullOrEmpty()) {
            loadImage(lastSeed)
        }



        val button = findViewById<Button>(R.id.new_image_button)
        button.setOnClickListener { loadImage() }
    }

    private fun loadImage(seed : String = randomSeed()) {
        val image = findViewById<ImageView>(R.id.random_image)
        Glide.with(this)
            .load("https://picsum.photos/seed/$seed/500")
            .timeout(10000)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    val error = e?.rootCauses?.firstOrNull()
                    if(error is HttpException) {
                        Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_LONG).show()
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    prefs.edit().putString("seed", seed).apply()
                    return false
                }

            })
            .into(image)
    }

    private fun randomSeed(): String {
        val allowedChars = ('a'..'z') + (0..9)
        return (1..5)
            .map { allowedChars.random() }
            .joinToString("")
    }

}
