
package com.example.user.pdashiveluch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResultActivity extends Activity {

    public static final String RESULT = "result";
    public static String mess=" ";
    String s;
    String[] allcodes = {"11111", "22222", "33333", "44444", "55555", "66666", "77777",
            "88888", "99999", "00000", "10121", "10122", "10123", "10124", "10125", "12321", "15151", "16161"};

    String [] results = {"Сталкерская куртка","Кольчужная куртка бандитов", "Броня \"Долга\"", "Броня \"Свободы\"", "Комбинезон  \"Монолита\"",
            "Халат  ученых", "Комбинезон \"СЕВА\"","Комбинезон \"Заря\"","Комбинезон \"ЭКОЛОГ\"",
            "Броня военных","Артефакт ОГНЕННЫЙ ШАР","Артефакт ЗОЛОТАЯ РЫБКА",
            "Артефакт ДУША","Артефакт БАТАРЕЙКА","Артефакт МЕДУЗА","Ремонт костюма", "Восстановление антирада","Восстановление аптечек"};
   // int[] suites = {R.drawable.kurt, R.drawable.duty, R.drawable.bandit,
          //  R.drawable.freedom, R.drawable.science, R.drawable.monolitst, R.drawable.seva,
         //   R.drawable.zaria, R.drawable.ecolog, R.drawable.bulat};

    //0. 11111 - сталкерская куртка
    //1. 22222 - бандитская куртка
    //2. 33333 - броня Долга
    //3. 44444 - броня Свободы
    //4. 55555 - Броня Монолита
    //5. 66666 - халат ученого
    //6. 77777 - костюм Сева
    //7. 88888 - костюм Заря
    //8. 99999 - костюм Эколог
    //9. 00000 - бронекостюм "Булат"
    //10. 10121 - артефакт Огненный шар
    //11. 10122 - артефакт Золотая рыбка
    //12. 10123 - артефакт Батарейка
    //13. 10124 - артефакт Медуза
    //14. 10125 - артефакт Душа
    //15. 12321 - ремонт костюма
    //16. 15151 - восстановление антирада
    //17. 16161 - восстановление аптечек
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        s = intent.getStringExtra(RESULT);
        TextView tw = new TextView(this);
        tw.setGravity(Gravity.CENTER);
        tw.setTextSize(26);
        if (s!=null) {
            tw.setText(s);
            for (int i=0;i<allcodes.length;i++)
            {if (s.equals(allcodes[i])) {tw.setText("НАЙДЕНО: " + "\n" + results[i]);}
            }
         if (s.equals(allcodes[10])||s.equals(allcodes[11])||s.equals(allcodes[12])||s.equals(allcodes[13])||s.equals(allcodes[14]))
            {
                pda.artslot.setVisibility(View.VISIBLE);
            }
            ResultActivity.mess=s.toString();
        }
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ResultActivity.mess=s.toString();
                pda.qr_result=1;

                finish();
            }
        });

        setContentView(tw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
