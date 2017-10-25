package com.example.liudong.archdemo.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.liudong.archdemo.databinding.RepoItemBinding
import com.example.liudong.archdemo.vo.Repo

/**
 * A RecyclerView adapter for [Repo] class.
 */
class RepoListAdapter(
        private val showFullName: Boolean,
        private val repoClickCallback: ((repo: Repo) -> Unit)?
) : DataBoundListAdapter<Repo, RepoItemBinding>() {

    override fun createBinding(parent: ViewGroup): RepoItemBinding {
        val binding = RepoItemBinding.inflate(LayoutInflater.from(parent.context))
        binding.showFullName = showFullName
        binding.root.setOnClickListener({
            val repo = binding.repo
            if (repo != null) {
                repoClickCallback?.invoke(repo)
            }
        })
        return binding
    }

    override fun bind(binding: RepoItemBinding, item: Repo) {
        binding.repo = item
    }

    override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem.stars == newItem.stars
    }
}
