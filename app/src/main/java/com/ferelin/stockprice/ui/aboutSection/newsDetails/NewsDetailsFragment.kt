package com.ferelin.stockprice.ui.aboutSection.newsDetails

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.ferelin.stockprice.databinding.FragmentNewsDetailsBinding
import com.google.android.material.transition.MaterialContainerTransform

class NewsDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentNewsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNewsDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            mBinding.apply {
                /*textViewHeadline.transitionName = it[sHeadlineName].toString()
                textViewSummary.transitionName= it[sSumaryName].toString()
                textViewDate.transitionName = it[sDateName].toString()
                textViewUrl.transitionName = it[sUrlName].toString()
                textViewSource.transitionName = it[sSourceName].toString()
                textViewBrowserHint.transitionName = it[sHintName].toString()
                rootConstraint.transitionName = it[sRootName].toString()*/
            }
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        arguments?.let {
            mBinding.apply {
                textViewHeadline.text = it[sHeadlineKeyStr].toString()
                textViewSummary.text = it[sSummaryKeyStr].toString()
                textViewDate.text = it[sDateKeyStr].toString()
                textViewUrl.text = it[sUrlKeyStr].toString()
                textViewSource.text = it[sSourceStr].toString()

                textViewUrl.setOnClickListener {
                    // todo open url
                }
            }
        }

        mBinding.imageViewIconClose.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    companion object {
        private const val sSourceStr = "source"
        private const val sHeadlineKeyStr = "headline"
        private const val sSummaryKeyStr = "summary"
        private const val sDateKeyStr = "date"
        private const val sUrlKeyStr = "url"

        private const val sHintName = "hint name"
        private const val sSourceName = "source name"
        private const val sHeadlineName = "haedlindasde"
        private const val sSumaryName = "summaryname"
        private const val sDateName = "datename"
        private const val sUrlName = "url name"
        private const val sRootName = "rootName"

        fun newInstance(
            source: String,
            headline: String,
            summary: String,
            date: String,
            url: String,
            sourceName: String,
            headlineName: String,
            summaryName: String,
            dateName: String,
            urlName: String,
            hintName: String,
            rootName: String
        ): NewsDetailsFragment {
            return NewsDetailsFragment().apply {
                arguments = bundleOf(
                    sSourceStr to source,
                    sHeadlineKeyStr to headline,
                    sSummaryKeyStr to summary,
                    sDateKeyStr to date,
                    sUrlKeyStr to url,

                    sSourceName to sourceName,
                    sHeadlineName to headlineName,
                    sSumaryName to summaryName,
                    sDateName to dateName,
                    sUrlName to urlName,
                    sHintName to hintName,
                    sRootName to rootName

                )
            }
        }
    }
}