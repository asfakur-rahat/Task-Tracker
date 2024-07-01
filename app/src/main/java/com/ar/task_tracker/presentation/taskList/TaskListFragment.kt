package com.ar.task_tracker.presentation.taskList

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ar.task_tracker.R
import com.ar.task_tracker.databinding.FragmentTaskListBinding
import com.ar.task_tracker.domain.model.Task
import com.ar.task_tracker.presentation.dialogs.ModalBottomSheet
import com.ar.task_tracker.presentation.dialogs.ModalBottomSheetListener
import com.ar.task_tracker.presentation.dialogs.getTask
import com.ar.task_tracker.presentation.dialogs.saveTask
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskListFragment : Fragment(), ModalBottomSheetListener {
    private val viewModel: TaskListViewModel by viewModels()
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskListAdapter

    private var saveClickCounter = 0
    private var task: Task? = null

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
        adapter =
            TaskListAdapter {
                if (saveClickCounter++ == 0) {
                    showOptions(it)
                    viewModel.viewModelScope.launch {
                        delay(500)
                        saveClickCounter = 0
                    }
                }
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
            gotoAddTask()
        }
        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.search -> {
                    gotoSearch()
                }
            }
            true
        }
    }

    private fun gotoAddTask() {
        findNavController().navigate(TaskListFragmentDirections.actionTaskListFragmentToAddTaskFragment())
    }

    private fun gotoSearch() {
        findNavController().navigate(TaskListFragmentDirections.actionTaskListFragmentToSearchTaskFragment())
    }

    // LiveDataObserver
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
    private fun showLoader() {
        binding.progressBar.visibility = View.VISIBLE
        binding.addTaskBtn.visibility = View.GONE
        binding.mainView.visibility = View.GONE
    }

    private fun hideLoader() {
        binding.progressBar.visibility = View.GONE
        binding.mainView.visibility = View.VISIBLE
        binding.addTaskBtn.visibility = View.VISIBLE
    }

    // BottomSheet Click Handler
    private fun showOptions(task: Task) {
        saveTask(requireContext(), task)
        this@TaskListFragment.task = getTask(requireContext())
        val bottomSheet = ModalBottomSheet.newInstance()
        bottomSheet.setTargetFragment(this, 0)
        bottomSheet.show(parentFragmentManager, ModalBottomSheet.TAG)
    }

    private fun completeTask(task: Task) {
        if (task.status) {
            Toast
                .makeText(requireContext(), "The task is already completed", Toast.LENGTH_SHORT)
                .show()
        } else {
            saveTask(requireContext(), task.copy(status = true))
            viewModel.markTaskAsDone(task.copy(status = true))
        }
    }

    // Navigation to Details Page
    private fun gotoDetails(task: Task) {
        findNavController().navigate(
            TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(
                task,
            ),
        )
    }

    // Navigation to Edit page
    private fun gotoEdit(task: Task) {
        findNavController().navigate(
            TaskListFragmentDirections.actionTaskListFragmentToEditTaskFragment(
                task,
            ),
        )
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

    override fun onEdit() {
        gotoEdit(getTask(requireContext())!!)
    }

    override fun onDetails() {
        gotoDetails(getTask(requireContext())!!)
    }

    override fun onCompleted() {
        completeTask(getTask(requireContext())!!)
    }
}
