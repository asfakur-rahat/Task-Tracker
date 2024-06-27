package com.ar.task_tracker.presentation.taskList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentTaskListBinding
import com.ar.task_tracker.domain.model.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment() {

    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentTaskListBinding.bind(view)
        adapter = TaskListAdapter()
        super.onViewCreated(view, savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        viewModel.taskList.observe(viewLifecycleOwner){ taskList ->
            initView(taskList)
        }
        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                binding.progressBar.visibility = View.VISIBLE
                binding.rvTaskList.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.rvTaskList.visibility = View.VISIBLE
            }
        }
    }

    private fun initView(taskList: List<Task>?) {
        binding.rvTaskList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL , false)
        binding.rvTaskList.adapter = adapter
        adapter.submitList(taskList)
    }
}