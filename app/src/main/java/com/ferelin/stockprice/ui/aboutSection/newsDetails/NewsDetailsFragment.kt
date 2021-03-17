package com.ferelin.stockprice.ui.aboutSection.newsDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.stockprice.databinding.FragmentNewsDetailsBinding

class NewsDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentNewsDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNewsDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            mBinding.apply {
                textViewHeadline.text = it[HEADLINE_STR_KEY].toString()
                textViewSummary.text = it[SUMMARY_STR_KEY].toString()
                textViewDate.text = it[DATE_STR_KEY].toString()
                textViewUrl.text = it[BROWSER_URL_STR_KEY].toString()
                val imageUrl = it[IMAGE_URL_STR_KEY].toString()

                Glide
                    .with(this@NewsDetailsFragment)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageViewNews)
            }
        }
    }

    companion object {
        const val HEADLINE_STR_KEY = "headline"
        const val SUMMARY_STR_KEY = "summary"
        const val DATE_STR_KEY = "date"
        const val BROWSER_URL_STR_KEY = "browser"
        const val IMAGE_URL_STR_KEY = "image"

        fun newInstance(args: Bundle): NewsDetailsFragment {
            return NewsDetailsFragment().apply { arguments = args }
        }
    }
}