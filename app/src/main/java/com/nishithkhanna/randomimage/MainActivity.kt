package com.nishithkhanna.randomimage

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import com.nishithkhanna.randomimage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var circularProgress: CircularProgressDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getPreferences(MODE_PRIVATE)

        circularProgress = CircularProgressDrawable(this)
        circularProgress.strokeWidth = 5f
        circularProgress.centerRadius = 100f
        circularProgress.setColorSchemeColors(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        circularProgress.start()

        val lastSeed = prefs.getString("seed", "")
        if (!lastSeed.isNullOrEmpty()) {
            loadImage(lastSeed)
        } else {
            binding.randomImage.setImageDrawable(getDrawable(R.drawable.placeholder))
        }
        binding.newImageButton.setOnClickListener { loadImage() }




    }

    private fun loadImage(seed: String = randomSeed()) {
        val lastSeed = prefs.getString("seed", "")
        Glide.with(binding.root)
            .load("https://picsum.photos/seed/$seed/500")
            .timeout(10000)
            .centerCrop()
            .transform(RoundedCorners(20))
            .error("https://picsum.photos/seed/$lastSeed/500")
            .placeholder(circularProgress)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    val error = e?.rootCauses?.firstOrNull()
                    if (error is HttpException) {
                        Snackbar.make(binding.root, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show()
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
            .into(binding.randomImage)
    }

    private fun randomSeed(): String {
        val allowedChars = ('a'..'z') + (0..9)
        return (1..5)
            .map { allowedChars.random() }
            .joinToString("")
    }

}
