package com.ar.task_tracker.presentation.taskList

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_task_list, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding = FragmentTaskListBinding.bind(view)
        adapter = TaskListAdapter {
                showOptions(it)
            }
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        viewModel.initList()
    }

    // Listener for buttons
    private fun initListener() {
        binding.addTaskBtn.setOnClickListener {
            findNavController().navigate(TaskListFragmentDirections.actionTaskListFragmentToAddTaskFragment())
        }
    }

    //LiveDataObserver
    private fun initObserver() {
        viewModel.taskList.observe(viewLifecycleOwner) { taskList ->
            initView(taskList)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                showLoader()
            } else {
                hideLoader()
            }
        }
    }

    // for show and hide loader
    private fun showLoader(){
        binding.progressBar.visibility = View.VISIBLE
        binding.rvTaskList.visibility = View.GONE
    }
    private fun hideLoader(){
        binding.progressBar.visibility = View.GONE
        binding.rvTaskList.visibility = View.VISIBLE
    }

    //BottomSheet Click Handler
    private fun showOptions(task: Task) {
        val bottomSheet = ModalBottomSheet(
            onEdit = { gotoEdit(task) },
            onDetails = { gotoDetails(task) },
            onCompleted = { completeTask(task) }
        )
        bottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)
    }

    private fun completeTask(task: Task) {
        if(task.status){
            Toast.makeText(requireContext(), "The task in already completed", Toast.LENGTH_SHORT).show()
        }else{
            viewModel.markTaskAsDone(task)
        }
    }

    //Navigation to Details Page
    private fun gotoDetails(task: Task) {
        findNavController().navigate(
            TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(
                task
            )
        )
    }
    //Navigation to Edit page
    private fun gotoEdit(task: Task) {
        findNavController().navigate(
            TaskListFragmentDirections.actionTaskListFragmentToEditTaskFragment(
                task
            )
        )
    }

    private fun initView(taskList: List<Task>?) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvTaskList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
            binding.rvTaskList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        binding.rvTaskList.adapter = adapter
        adapter.submitList(taskList)
    }
}