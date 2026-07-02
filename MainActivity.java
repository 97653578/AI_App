package com.shoujibiancheng.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText etInput;
    private Button btnSend, btnPackage;
    private RecyclerView rvMessages;
    private LinearLayout progressLayout;
    private TextView tvProgress;
    private ProgressBar progressBar;
    private List<Map<String, String>> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput = findViewById(R.id.etInput);
        btnSend = findViewById(R.id.btnSend);
        btnPackage = findViewById(R.id.btnPackage);
        rvMessages = findViewById(R.id.rvMessages);
        progressLayout = findViewById(R.id.layoutProgress);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);

        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages);
        rvMessages.setAdapter(adapter);

        addMessage("👋 欢迎！输入需求生成模拟代码", false);

        btnSend.setOnClickListener(v -> sendMessage());
        btnPackage.setOnClickListener(v -> buildApk());
    }

    private void sendMessage() {
        String input = etInput.getText().toString().trim();
        if (input.isEmpty()) return;
        addMessage(input, true);
        etInput.setText("");
        addMessage("⏳ 生成中...", false);
        btnSend.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Thread.sleep(1500);
                String response = "// 模拟代码：\n// \"" + input + "\"\n\npublic class Demo {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, AI!\");\n    }\n}";
                mainHandler.post(() -> {
                    if (!messages.isEmpty() && messages.get(messages.size()-1).get("content").contains("⏳")) {
                        messages.remove(messages.size()-1);
                        adapter.notifyItemRemoved(messages.size());
                    }
                    addMessage(response, false);
                    btnSend.setEnabled(true);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    addMessage("❌ 错误: " + e.getMessage(), false);
                    btnSend.setEnabled(true);
                });
            }
        });
    }

    private void addMessage(String content, boolean isUser) {
        Map<String, String> msg = new HashMap<>();
        msg.put("content", content);
        msg.put("isUser", String.valueOf(isUser));
        messages.add(msg);
        mainHandler.post(() -> {
            adapter.notifyItemInserted(messages.size()-1);
            rvMessages.scrollToPosition(messages.size()-1);
        });
    }

    private void buildApk() {
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        tvProgress.setText("打包中...");
        btnPackage.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                for (int i = 10; i <= 100; i+=10) {
                    Thread.sleep(300);
                    int finalI = i;
                    mainHandler.post(() -> {
                        progressBar.setProgress(finalI);
                        tvProgress.setText(finalI + "%");
                    });
                }
                mainHandler.post(() -> {
                    progressLayout.setVisibility(View.GONE);
                    btnPackage.setEnabled(true);
                    Toast.makeText(MainActivity.this, "✅ 打包完成（模拟）", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressLayout.setVisibility(View.GONE);
                    btnPackage.setEnabled(true);
                    Toast.makeText(MainActivity.this, "打包失败", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        private List<Map<String, String>> data;
        MessageAdapter(List<Map<String, String>> data) { this.data = data; }
        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_message, parent, false);
            return new ViewHolder(v);
        }
        @Override public void onBindViewHolder(ViewHolder holder, int position) {
            Map<String, String> item = data.get(position);
            holder.tvMessage.setText(item.get("content"));
            boolean isUser = Boolean.parseBoolean(item.get("isUser"));
            holder.layoutUser.setVisibility(isUser ? View.VISIBLE : View.GONE);
            holder.layoutAssistant.setVisibility(isUser ? View.GONE : View.VISIBLE);
        }
        @Override public int getItemCount() { return data.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMessage;
            LinearLayout layoutUser, layoutAssistant;
            ViewHolder(View v) {
                super(v);
                tvMessage = v.findViewById(R.id.tvMessage);
                layoutUser = v.findViewById(R.id.layoutUser);
                layoutAssistant = v.findViewById(R.id.layoutAssistant);
            }
        }
    }
      }
