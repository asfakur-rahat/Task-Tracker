package com.ar.task_tracker.presentation.taskList

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentTaskListBinding
import com.ar.task_tracker.domain.model.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment() {
    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskListAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_task_list, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding = FragmentTaskListBinding.bind(view)
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheet))
        adapter =
            TaskListAdapter {
                //taskClicked(it)
            }
        super.onViewCreated(view, savedInstanceState)
        initObserver()
    }

//    private fun taskClicked(it: Task) {
//        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        } else {
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        }
//    }

    private fun initObserver() {
        viewModel.taskList.observe(viewLifecycleOwner) { taskList ->
            initView(taskList)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvTaskList.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvTaskList.visibility = View.VISIBLE
            }
        }
    }

    private fun initView(taskList: List<Task>?) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvTaskList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
            binding.rvTaskList.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        binding.rvTaskList.adapter = adapter
        adapter.submitList(taskList)
    }
}
