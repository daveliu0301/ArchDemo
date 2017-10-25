package com.example.liudong.archdemo.ui.repo

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.liudong.archdemo.databinding.ContributorItemBinding
import com.example.liudong.archdemo.ui.common.DataBoundListAdapter
import com.example.liudong.archdemo.vo.Contributor


class ContributorAdapter(
        private val contributorClickCallback: ((contributor: Contributor) -> Unit)?
) : DataBoundListAdapter<Contributor, ContributorItemBinding>() {

    override fun createBinding(parent: ViewGroup): ContributorItemBinding {
        val binding = ContributorItemBinding.inflate(LayoutInflater.from(parent.context))
        binding.root.setOnClickListener({
            val contributor = binding.contributor
            if (contributor != null) {
                contributorClickCallback?.invoke(contributor)
            }
        })
        return binding
    }

    override fun bind(binding: ContributorItemBinding, item: Contributor) {
        binding.contributor = item
    }

    override fun areItemsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
        return oldItem.login == newItem.login
    }

    override fun areContentsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
        return oldItem.contributions == newItem.contributions
    }
}