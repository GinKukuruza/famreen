package com.example.famreen.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.states.States
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.SearchAdapter
import com.example.famreen.application.items.SearchItem
import com.example.famreen.application.items.SearchViewItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.utils.observers.UpdateObserver
import com.example.famreen.application.viewmodels.SearchViewModel
import com.example.famreen.databinding.FragmentSearchBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.utils.extensions.set
import javax.inject.Inject

class SearchFragment : Fragment() {
    //ui
    private val mViewModel: SearchViewModel = SearchViewModel()
    private var mSearchAdapter: SearchAdapter? = null
    private lateinit var mBinding: FragmentSearchBinding
    @Inject lateinit var mFirebaseProvider: FirebaseProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        App.appComponent.inject(this@SearchFragment)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        mBinding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getState().observe(viewLifecycleOwner,androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> { }
                is States.LoadingState -> { }
                is States.ErrorState -> {
                    Toast.makeText(requireContext(),it.msg, Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
            }
        })
        val position = AppPreferences.getProvider()!!.readSearchEngine()
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(requireContext(), R.array.search_engine, R.layout.spinner_search_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spinnerSearchSearchEngines.adapter = adapter
        mBinding.spinnerSearchSearchEngines.setSelection(position)
        mBinding.spinnerSearchSearchEngines.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, itemSelected: View, selectedItemPosition: Int, selectedId: Long) {
                AppPreferences.getProvider()!!.writeSearchEngine(selectedItemPosition)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        updateAdapter(mViewModel.getSearchList())
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(mFirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }

    private fun updateAdapter(items: List<SearchItem>) {
        updateView()
        if (mSearchAdapter == null) {
            mSearchAdapter = SearchAdapter(object : UpdateObserver{
                override fun update() {
                    updateView()
                }
            }, items)
            mBinding.rvSearch.adapter = mSearchAdapter
        } else mSearchAdapter!!.notifyDataSetChanged()
    }
    /**
     * Функция обновляет основные элементы экрана: иконка браузера и название
     * **/
    fun updateView() {
        val searchViewItem = SearchViewItem()
        val name = AppPreferences.getProvider()!!.readSearchBrowserName()
        val packageName = AppPreferences.getProvider()!!.readSearchPackageBrowserName()
        searchViewItem.mBrowserName = name
        for (item in mViewModel.getSearchList()) {
            if (packageName == item.mPackageName) {
                mBinding.ivSearchImage.setImageDrawable(item.mImage)
                break
            }
        }
        mBinding.item = searchViewItem
    }
}