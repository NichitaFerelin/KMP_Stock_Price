package com.ferelin.stockprice.ui.aboutSection.newsDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.databinding.FragmentNewsDetailsBinding
import com.ferelin.stockprice.ui.aboutSection.news.NewsFragment

class NewsDetailsFragment(
    private val mCoroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : Fragment() {

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
                textViewHeadline.text = it[NewsFragment.KEY_HEADLINE].toString()
                textViewSummary.text = it[NewsFragment.KEY_SUMMARY].toString()
                textViewDate.text = it[NewsFragment.KEY_DATE].toString()
                textViewUrl.text = it[NewsFragment.KEY_BROWSER_URL].toString()
                val imageUrl = it[NewsFragment.KEY_IMAGE_URL].toString()

                Glide
                    .with(this@NewsDetailsFragment)
                    .load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageViewNews)
            }
        }
    }

    companion object {
        fun newInstance(args: Bundle): NewsDetailsFragment {
            return NewsDetailsFragment().apply {
                arguments = args
            }
        }
    }
}