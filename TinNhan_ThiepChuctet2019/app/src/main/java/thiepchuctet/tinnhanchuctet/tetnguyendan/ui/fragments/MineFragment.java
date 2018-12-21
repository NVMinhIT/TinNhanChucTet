package thiepchuctet.tinnhanchuctet.tetnguyendan.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import thiepchuctet.tinnhanchuctet.tetnguyendan.MyApplication;
import thiepchuctet.tinnhanchuctet.tetnguyendan.R;
import thiepchuctet.tinnhanchuctet.tetnguyendan.adapters.MessageAdapter;
import thiepchuctet.tinnhanchuctet.tetnguyendan.database.sharedprf.SharedPrefsImpl;
import thiepchuctet.tinnhanchuctet.tetnguyendan.database.sqlite.DatabaseHelper;
import thiepchuctet.tinnhanchuctet.tetnguyendan.database.sqlite.TableEntity;
import thiepchuctet.tinnhanchuctet.tetnguyendan.databinding.FragmentMsgMineBinding;
import thiepchuctet.tinnhanchuctet.tetnguyendan.listeners.OnItemClickListener;
import thiepchuctet.tinnhanchuctet.tetnguyendan.listeners.OnItemLongClickListener;
import thiepchuctet.tinnhanchuctet.tetnguyendan.models.Message;
import thiepchuctet.tinnhanchuctet.tetnguyendan.ui.activities.MainActivity;
import thiepchuctet.tinnhanchuctet.tetnguyendan.utils.Navigator;

public class MineFragment extends Fragment implements OnItemClickListener, View.OnClickListener, OnItemLongClickListener<Message> {

    private FragmentMsgMineBinding mBinding;
    private List<Message> mMessages;
    private MainActivity mMainActivity;
    private Navigator mNavigator;
    private MessageAdapter mAdapter;
    private SharedPrefsImpl mSharedPrefs;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_msg_mine, container, false);
        initUI();
        return mBinding.getRoot();
    }

    private void initUI() {
        mSharedPrefs = new SharedPrefsImpl(mMainActivity);
        mNavigator = new Navigator(mMainActivity);
        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnAddNew.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mMainActivity);
        mMessages = DatabaseHelper.getInstance(MyApplication.getInstance()).getListMsg(TableEntity.TBL_MY_MESSAGE);
        mSharedPrefs.putListMsg(mMessages);
        if (mMessages.size() == 0) {
            mBinding.txtNone.setVisibility(View.VISIBLE);
        } else {
            mBinding.txtNone.setVisibility(View.GONE);
        }
        mAdapter = new MessageAdapter(mMainActivity, mMessages);
        mAdapter.setOnItemClick(this);
        mAdapter.setOnItemLongClick(this);
        mBinding.recyclerView.setLayoutManager(linearLayoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int pos) {
        MessageFragment messageFragment = MessageFragment.newInstance(mMessages, pos, pos + 1, false);
        mNavigator.addFragment(R.id.main_container, messageFragment, true,
                Navigator.NavigateAnim.NONE, MessageFragment.class.getSimpleName());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                mMainActivity.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btn_add_new:
                EditMessageFragment fragment = EditMessageFragment.newInstance(new Message(0, ""), true);
                mNavigator.addFragment(R.id.main_container, fragment, true,
                        Navigator.NavigateAnim.NONE, EditMessageFragment.class.getSimpleName());
                break;
        }
    }

    @Override
    public void onItemLongClick(Message message) {
        confirmDelete(message);
    }

    private void confirmDelete(final Message message) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mMainActivity);
        dialog.setTitle(R.string.notifi);
        dialog.setMessage(R.string.delete_confirm_msg);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (deleteMsg(TableEntity.TBL_MY_MESSAGE, message) > 0) {
                    int size = mMessages.size();
                    for (int j = 0; j < size; j++) {
                        if (mMessages.get(j).getId() == message.getId()) {
                            mMessages.remove(j);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    mSharedPrefs.putListMsg(mMessages);
                    mNavigator.showToast(R.string.delete_success);
                } else {
                    mNavigator.showToast(R.string.delete_failed);
                }

            }
        });

        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();
    }

    private int deleteMsg(String tblName, Message message) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(MyApplication.getInstance());
        return databaseHelper.deleteMessage(tblName, message.getId());
    }
}
