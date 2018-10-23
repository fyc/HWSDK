package com.yiyou.gamesdk.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gamesdk.shouyouba.tzsy.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * 文件浏览器。
 * Created by chenshuide on 2/11/15.
 */
public class FileBrowserActivity extends FragmentActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String fragment_tag = "fragment_tag";

    private ListView mListView;

    private ViewGroup mRootView;

    private TextView mTvPath;

    private File mCurDir;

    private MyAdapter mAdapter;

    private File mRootDir;

    private ShowContentFragment mShowContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filebrowser);

        initViews();

        // 设置初始浏览路径
        mRootDir = getFilesDir().getParentFile();
        browserDir(mRootDir);
    }

    private void initViews() {
        mRootView = (ViewGroup) findViewById(android.R.id.content);

        // title bar
        findViewById(R.id.filepath_back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                upperDirOrFinish();
            }
        });

        // path show
        mTvPath = (TextView) findViewById(R.id.tv_path);

        // list content
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    private void browserDir(File dirFile) {
        if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }

        mCurDir = dirFile;
        mTvPath.setText(dirFile.getAbsolutePath());

        // list all files
        String[] subFiles = dirFile.list();
        if (subFiles == null) {
            subFiles = new String[0];
        }
        mAdapter.setFiles(subFiles);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String fileName = (String) mAdapter.getItem(position);
        File file = new File(mCurDir, fileName);
        if (file.isDirectory()) {
            browserDir(new File(mCurDir, fileName));
        } else {
            showFileContent(file);
        }
    }

    @Override
    public void onBackPressed() {
        upperDirOrFinish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO 暂未实现。需要时实现
//        String fileName = (String) mAdapter.getItem(position);
//        File file = new File(mCurDir, fileName);
//        if(file.isFile()) {
//            copy2Sdcard(file);
//            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getAbsolutePath()));
//            startActivity(i);
//            return true;
//        }
        return false;
    }

    private class MyAdapter extends BaseAdapter {

        String[] files = new String[0];

        public void setFiles(String[] files) {
            this.files = files;
        }

        @Override
        public int getCount() {
            return files.length;
        }

        @Override
        public Object getItem(int position) {
            return files[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View layout = getLayoutInflater().inflate(R.layout.listitem_filebrowser, parent, false);
            ImageView ivType = (ImageView) layout.findViewById(R.id.type);
            int imgId = new File(mCurDir, files[position]).isDirectory() ? R.drawable.file_folder : R.drawable.file_text;
            ivType.setImageResource(imgId);

            TextView tvName = (TextView) layout.findViewById(R.id.filepath);
            tvName.setText(files[position]);
            return layout;
        }
    }

    private void showFileContent(File file) {
        //read data
        byte[] data = readBytes(file);

        //show data
        if (mShowContentFragment == null) {
            mShowContentFragment = new ShowContentFragment();
        }

        if (data == null) {
            // show error
            mShowContentFragment.setData("empty".getBytes());
        } else {
            mShowContentFragment.setData(data);
        }
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, mShowContentFragment, fragment_tag).commit();
    }

    private void upperDirOrFinish() {
        //如果当前显示文件内容则隐藏
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragment_tag);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commit();
            return;
        }

        //如果当前在根目录，则返回上一级页面；否则返回上一级目录
        if (mCurDir.getAbsolutePath().equals(mRootDir.getAbsolutePath())) {
            finish();
        } else {
            browserDir(mCurDir.getParentFile());
        }
    }


    public static byte[] readBytes(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        byte[] result = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] buf = new byte[(int) file.length()];
            int readTotal = 0, read = 0;
            while ((read = bis.read(buf, readTotal, buf.length - readTotal)) != -1) {
                readTotal += read;
                if (readTotal >= buf.length) {
                    break;
                }
            }
            result = buf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

}
