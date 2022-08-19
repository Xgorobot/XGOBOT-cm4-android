package com.luwu.xgo_robot.mFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luwu.xgo_robot.R;

public class ListviewFragment extends Fragment {
    private String[] mListArray;//listview数组
    private ListView mListView;
    private ListAdapterProgram mListAdapter;
    private IListviewFragmentListener mListener;

    public ListviewFragment() {

    }

    public ListviewFragment(String[] listArray) {
        super();
        mListArray = listArray;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListAdapter = new ListAdapterProgram(this.getContext(), mListArray);
        mListView = view.findViewById(R.id.listviewFragmentList);
        mListView.setAdapter(mListAdapter);
        setListViewClick();
    }

    public void setListArray(String[] listArray) {
        this.mListArray = listArray;
        if (mListAdapter != null) {
            this.mListAdapter.setListArray(listArray);
            mListAdapter.setListChoice(-1);
            mListAdapter.notifyDataSetInvalidated();
        }
        setListViewClick();
    }
    public void setNoListChioce(){//设置未选择
        if (mListAdapter != null) {
            mListAdapter.setListChoice(-1);
            mListAdapter.notifyDataSetInvalidated();
        }
    }

    public void setItemListener(IListviewFragmentListener listener) {
        mListener = listener;
    }

    public interface IListviewFragmentListener {
        void onItemClick(int position);
    }

    private void setListViewClick() {
        if (mListView != null) {
            if ((mListArray != null) && (mListArray.length != 0)) {
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mListener != null) {
                            mListener.onItemClick(position);
                        }
                        mListAdapter.setListChoice(position);
                        mListAdapter.notifyDataSetInvalidated();
                    }
                });
            } else {
                mListView.setClickable(false);
            }
        }
    }
}
