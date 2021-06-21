package com.nbk.test.ui.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nbk.test.R;
import com.nbk.test.databinding.FragmentDashboardBinding;
import com.nbk.test.exception.CalError;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private EditText editLine;
    private TextView preView;
    private BigDecimal result = new BigDecimal("0");
    //一个存储参与运算数字的工具, 当下一个插入的运算符等级低于当前时,将计算之前的结果
    private Stack<BigDecimal> numStack = new Stack<>();
    private boolean calError;
    boolean done = false;
    private CalOption lastOption;

    enum CalOption {
        // 需要两个数, type=2
        DIVIDE("/"),
        MULTI("*"),
        SUB("-", 6),
        ADD("+", 6),
        // 两个数构成一个数?=>也算是两个数的操作,
        POINT("."),

        // 需要一个数 type=1
        POW("^", 1),
        PERCENT("%", 1),

        // 不需要任何就可以触发的操作 type=0
        CLEAR("C", 0),
        // 这两个也可以归为需要至少一个数才可以触发?
        ENTER("=", 0),
        DEL("←", 0),
        ;
        private String name;
        private int type;

        CalOption(String name, int type) {
            this.name = name;
            this.type = type;
        }

        CalOption(String name) {
            this.name = name;
            this.type = 2;
        }

        private static final Map<String, CalOption> map = new HashMap<>();

        static {
            for (CalOption i : CalOption.values()) {
                map.put(i.name, i);
            }
        }

        /**
         * 通过符号解析为操作符
         *
         * @param name 符号
         * @return 指定的操作符
         * @throws ParseException 当解析不出时
         */
        public static CalOption parse(String name) throws ParseException {
            if (!map.containsKey(name))
                throw new ParseException("解析操作符失败", 0);
            return map.get(name);
        }

        public String getName() {
            return name;
        }

        public boolean is符号数() {
            return (type & 4) == 4;
        }

        public boolean is随意() {
            return type == 0;
        }

        public boolean is单目() {
            return (type & 1) == 1;
        }

        public boolean is双目() {
            return (type & 2) == 2;
        }
    }


    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        editLine = activity.findViewById(R.id.editLine);
        preView = activity.findViewById(R.id.preView);
        initCalBtnListener(activity);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    private void initCalBtnListener(FragmentActivity activity) {
        // 运算数
        ((Button) activity.findViewById(R.id.btn_0)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_1)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_2)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_3)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_4)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_5)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_6)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_7)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_8)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_9)).setOnClickListener(this);

        // 运算符
        ((Button) activity.findViewById(R.id.btn_clear)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_del)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_divide)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_mul)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_sub)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn__)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_add)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_enter)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_pow)).setOnClickListener(this);
        ((Button) activity.findViewById(R.id.btn_percent)).setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9: {
                CharSequence numText = ((Button) v).getText();
                Integer num = Integer.valueOf((String) numText);
                catNum(num);
            }
            break;
            default:
                try {
                    CalOption option = CalOption.parse((String) ((Button) v).getText());
                    doOption(option);
                } catch (ParseException e) {
                    Log.d("Cal", "无法解析的操作符");
                } catch (CalError error) {
                    setCalError();
                }
        }
//        if(!done) {
//            BigDecimal result = doneOption(false);
//            testOption(result.toString());
//        }else {
        testOption(this.result.toString());
//        }

    }

    private void testOption(String result) {
        if (!done)
            preView.setText(String.format("%s", result));
        else {
            preView.setText("");
            editLine.getText().clear();
            editLine.getText().insert(0, result);
        }
    }

    private void doOption(CalOption option) throws CalError {
        int selectionIndex = editLine.getSelectionStart();
        Editable text = editLine.getText();
        done = false;
        if (option.is随意()) {
            switch (option) {
                case CLEAR:
                    text.clear();
                    break;
                case DEL:
                    int lastCharIndex = selectionIndex;
                    if (lastCharIndex > 0)
                        text.delete(lastCharIndex - 1, lastCharIndex);
                    break;
                case ENTER:
                    if (editLine.getText().length() > 0 && !calError) {
                        doneOption(true);
                    }
                    break;
            }
        } /*else if(option.is符号数()){先暂时不加入讨论
            // +,-,.三个可以直接写在数字前面当符号
        }*/ else {//剩余的符号需要和数字连起来构成一段段的运算过程
            //各种操作数,,, 除了加减号和点之外都不可能出现在第一位
            if (text.length() == 0)
                throw new CalError();

            char c = text.subSequence(0, 1).charAt(0);
            if ('0' >= c || '9' <= c)
                throw new CalError();
            String selection = String.valueOf(getCharAt(editLine, selectionIndex - 1));
            try {
                CalOption.parse(selection);
                text.delete(selectionIndex - 1, selectionIndex);
            } catch (ParseException ignored) {
            }
            text.insert(editLine.getSelectionStart(), option.getName());
        }
    }

    private char getCharAt(EditText editText, int index) {
        char[] chars = new char[1];
        editText.getText().getChars(index, index + 1, chars, 0);
        return chars[0];
    }

    private void setCalError() {
        preView.setText("错误");
        calError = true;
    }

    private BigDecimal doneOption(boolean enter) {
        String str = editLine.getText().toString();
        String[] numArr = str.split("[/*\\-+^%]");
        List<BigDecimal> numList = Arrays.stream(numArr).map(BigDecimal::new).collect(Collectors.toList());

        Pattern p = Pattern.compile("[/*\\-+^%]");
        Matcher m = p.matcher(str);
        Queue<CalOption> optionStack = new LinkedList<>();
        while (m.find()) {
            try {
                optionStack.add(CalOption.parse(m.group()));
            } catch (ParseException ignored) {
            }
        }
        // 进行计算
        return calculator(numList, optionStack, enter);
    }

    private BigDecimal calculator(List<BigDecimal> numList, Queue<CalOption> optionQueue, boolean enter) {
        int sub = numList.size() - optionQueue.size();
        if (sub != 0 && sub != 1) {
            Log.d("error", "未知的操作数个数与计算数个数比例");
            throw new RuntimeException("未知的操作数个数与计算数个数比例");
        }
        // 开始压计算
        for (int i = 0; i < numList.size(); i++) {
            BigDecimal item = numList.get(i);
            if (i == 0) {
                numStack.push(item);
                continue;
            }
            CalOption option = optionQueue.poll();
            switch (option) {
                case PERCENT:
                    numStack.push(numStack.pop().divide(PERCENT));
                    break;
                case MULTI:
                    numStack.push(numStack.pop().multiply(item));
                    break;
                case DIVIDE:
                    numStack.push(numStack.pop().divide(item));
                    break;
                case SUB:
                    item = item.negate();
                case ADD:
                    numStack.push(item);
                    break;
            }
        }
        // 已完成第一遍运算, 完成了高阶运算和单目运算
        BigDecimal result = new BigDecimal("0");
        for (BigDecimal decimal : numStack) {
            result = result.add(decimal);
        }
        done = enter;
        this.result = result;
        return result;
    }

    private void catNum(Integer num) {
        int index = editLine.getSelectionStart();
        editLine.getText().insert(index, String.valueOf(num));
    }

    private static final BigDecimal PERCENT = new BigDecimal(100);
}