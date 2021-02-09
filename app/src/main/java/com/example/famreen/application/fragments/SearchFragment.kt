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
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.adapters.SearchAdapter
import com.example.famreen.application.interfaces.CallbackListener
import com.example.famreen.application.items.SearchItem
import com.example.famreen.application.items.SearchViewItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.viewmodels.SearchViewModel
import com.example.famreen.databinding.FragmentSearchBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.states.callback.ItemStates
import com.example.famreen.states.callback.ThrowableStates
import com.example.famreen.utils.extensions.set
import javax.inject.Inject

class SearchFragment : Fragment() {
    //ui
    @Inject
    lateinit var mViewModel: SearchViewModel
    private var mSearchAdapter: SearchAdapter? = null
    private lateinit var mBinding: FragmentSearchBinding
    private lateinit var mSearchList : List<SearchItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@SearchFragment)
        mSearchList = mViewModel.getSearchList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val position = AppPreferences.getProvider()!!.readSearchEngine()
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(requireContext(), R.array.search_engine, R.layout.spinner_search_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spinnerSearchSearchEngines.adapter = adapter
        mBinding.spinnerSearchSearchEngines.setSelection(position)
        mBinding.spinnerSearchSearchEngines.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, itemSelected: View?, selectedItemPosition: Int, selectedId: Long) {
                AppPreferences.getProvider()!!.writeSearchEngine(selectedItemPosition)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getState().observe(viewLifecycleOwner, {
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
        updateAdapter()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }

    private fun updateAdapter() {
        val name = AppPreferences.getProvider()!!.readSearchBrowserName()
        val packageName = AppPreferences.getProvider()!!.readSearchPackageBrowserName()
        if(mSearchAdapter == null){
            mSearchAdapter = SearchAdapter(mSearchList)
            mSearchAdapter?.setUpdateListener(object : CallbackListener<SearchItem>{
                override fun onItem(s: ItemStates.ItemState<SearchItem>) {
                    val item = s.item
                    updateUI(item.mName,item.mPackageName)
                }
                override fun onFailure(state: ThrowableStates) {}
            })
            updateUI(name,packageName)
            mBinding.rvSearch.adapter = mSearchAdapter
        }else{
            mBinding.rvSearch.adapter = mSearchAdapter
            updateUI(name,packageName)
        }
    }
    /**
     * Функция обновляет основные элементы экрана: иконка браузера и название
     * **/
    fun updateUI(name: String?,packageName: String?) {
        name?.let {
            val searchViewItem = SearchViewItem()
            searchViewItem.mBrowserName = name
            mBinding.item = searchViewItem
        }
        packageName?.let {
            for (item in mSearchList) {
                if (packageName == item.mPackageName) {
                    mBinding.ivSearchImage.setImageDrawable(item.mImage)
                    break
                }
            }
        }
    }
}