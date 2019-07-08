package com.yy.kaitian.yl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class CustomerProfilesEditActivity extends Activity {
    static final int DATE_DIALOG_ID = 999;
    //    private Button btnChangeDate;
//    private OnDateSetListener datePickerListener = new OnDateSetListener() {
//        public void onDateSet(DatePicker paramAnonymousDatePicker, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
//            CustomerProfilesEditActivity.this.year = paramAnonymousInt1;
//            CustomerProfilesEditActivity.this.month = paramAnonymousInt2;
//            CustomerProfilesEditActivity.this.day = paramAnonymousInt3;
//            CustomerProfilesEditActivity.this.mBirthday = (CustomerProfilesEditActivity.this.year + "-" + (1 + CustomerProfilesEditActivity.this.month) + "-" + CustomerProfilesEditActivity.this.day);
//            CustomerProfilesEditActivity.this.btnChangeDate = ((Button) CustomerProfilesEditActivity.this.findViewById(R.id.btnChangeDate));
//            CustomerProfilesEditActivity.this.btnChangeDate.setText(CustomerProfilesEditActivity.this.mBirthday);
//        }
//    };
    private int day;
    private String mBirthday;
    private CustomerProfilesClass mCustomerPrifiles = null;
    private String mDetectTime;
    private String mDiastolicPressure;
    private EditText mEditDiastolicPressure;
    private EditText mEditHeight;
    private EditText mEditMemo;
    private EditText mEditName;
    private EditText mEditPulse;
    private EditText mEditSystolicPressure;
    private EditText mEditWeight;
    private String mHeight;
    private int mId;
    private String mMaritalStatus;
    private String mMedicalHistory1;
    private String mMedicalHistory2;
    private String mMedicalHistory3;
    private String mMemo;//客服ID
    private String mName;
    private String mNativePlace;
    private Spinner mNativeSpinner;
    private String mProfession;//职业
    private Spinner mProfessionSpinner;
    private String mPulse;
    private RadioGroup mRadioMaritalStatusGroup;
    private RadioGroup mRadioSexGroup;
    private String mRegisterTime;
    private String mSex;
    private Spinner mSpinnerMedicalHistory1;
    private Spinner mSpinnerMedicalHistory2;
    private Spinner mSpinnerMedicalHistory3;
    private String mSystolicPressure;
    private String mWeight;
    private boolean mbModify;//是否修改
    private int month;
    private int year;


    private TextView tv_save;
    private LinearLayout ll_back;

    private TextView tv_male, tv_female;
    private TextView tv_unmarried, tv_married;
    private boolean isMale = true;
    private boolean isUnmarried = true;
    private EditText et_ChangeDate;

    private void AddNewProfile() {
//    if (this.mbModify);
        for (String str = "确定要修改客户档案?"; ; str = "确定要新增客户档案?") {
            Builder localBuilder = new Builder(this);
            localBuilder.setMessage(str).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    CustomerProfilesEditActivity.this.mRegisterTime = CustomerProfilesEditActivity.this.myGetCurrentTime();
                    if (CustomerProfilesEditActivity.this.addNewCustomerProfiles() == 0)
                        CustomerProfilesEditActivity.this.finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramAnonymousDialogInterface.cancel();
                }
            });
            localBuilder.create().show();
            return;
        }
    }

    private void CancelAddNewProfile() {
        Builder localBuilder = new Builder(this);
        localBuilder.setMessage("确定要退出客户档案的编辑?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                CustomerProfilesEditActivity.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        });
        localBuilder.create().show();
    }

    private void DelCustomerPrifile() {
        Builder localBuilder = new Builder(this);
        localBuilder.setMessage("确定要删除?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                if (CustomerProfilesEditActivity.this.delProfileById(CustomerProfilesEditActivity.this.mId))
                    CustomerProfilesEditActivity.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        });
        localBuilder.create().show();
    }

    private int addNewCustomerProfiles() {
        this.mName = this.mEditName.getText().toString();
        if (this.mName.length() == 0) {
            myPrompt("请输入名称！");
            return -1;
        }
        this.mNativePlace = this.mNativeSpinner.getSelectedItem().toString();
        this.mProfession = this.mProfessionSpinner.getSelectedItem().toString();

        if (isMale) {
            this.mSex = "Male";
        } else {
            this.mSex = "Female";
        }
        if (isUnmarried) {
            this.mMaritalStatus = "unmarried";
        } else {
            this.mMaritalStatus = "married";
        }
        this.mHeight = this.mEditHeight.getText().toString();
        if (this.mHeight.length() == 0) {
            myPrompt("请输入身高！");
            return -1;
        }
        if (!TDSUtils.isDigit(this.mHeight)) {
            myPrompt("身高输入错误，请输入整数！");
            return -1;
        }
        this.mWeight = this.mEditWeight.getText().toString();
        if (this.mWeight.length() == 0) {
            myPrompt("请输入体重！");
            return -1;
        }
        if (!TDSUtils.isDigit(this.mWeight)) {
            myPrompt("体重输入错误，请输入整数！");
            return -1;
        }
        this.mSystolicPressure = this.mEditSystolicPressure.getText().toString();
        if (this.mSystolicPressure.length() == 0) {
            myPrompt("请输入收缩压！");
            return -1;
        }
        if (!TDSUtils.isDigit(this.mSystolicPressure)) {
            myPrompt("收缩压输入错误，请输入整数！");
            return -1;
        }
        this.mDiastolicPressure = this.mEditDiastolicPressure.getText().toString();
        if (this.mDiastolicPressure.length() == 0) {
            myPrompt("请输入舒张压！");
            return -1;
        }
        if (!TDSUtils.isDigit(this.mDiastolicPressure)) {
            myPrompt("舒张压输入错误，请输入整数！");
            return -1;
        }
        this.mPulse = this.mEditPulse.getText().toString();
        if (this.mPulse.length() == 0) {
            myPrompt("请输入脉搏！");
            return -1;
        }
        if (!TDSUtils.isDigit(this.mPulse)) {
            myPrompt("脉搏输入错误，请输入整数！");
            return -1;
        }
        this.mMedicalHistory1 = this.mSpinnerMedicalHistory1.getSelectedItem().toString();
        this.mMedicalHistory2 = this.mSpinnerMedicalHistory2.getSelectedItem().toString();
        this.mMedicalHistory3 = this.mSpinnerMedicalHistory3.getSelectedItem().toString();
        this.mMemo = this.mEditMemo.getText().toString();
        if (this.mMemo.length() == 0) {
            myPrompt("请输入客户ID！");
            return -1;
        }
        if (!TDSUtils.isLetterDigit(this.mMemo)) {
            myPrompt("客户ID输入错误，请输入字母和数字！");
            return -1;
        }
        Log.e("zj", "mMame=" + this.mName + " birthday=" + this.mBirthday + " mNativePlace=" + this.mNativePlace + " mProfession=" + this.mProfession + " mSex=" + this.mSex + " mMaritalStatus=" + this.mMaritalStatus + " mHeight=" + this.mHeight + " mWeight=" + this.mWeight + " mSystolicPressure=" + this.mSystolicPressure + " mDiastolicPressure=" + this.mDiastolicPressure + " mPulse=" + this.mPulse + " mMedicalHistory1=" + this.mMedicalHistory1 + " mMedicalHistory2=" + this.mMedicalHistory2 + " mMedicalHistory3=" + this.mMedicalHistory3 + " mMemo=" + this.mMemo);
        this.mCustomerPrifiles.setName(this.mName);
        this.mCustomerPrifiles.setBirthday(this.mBirthday);
        this.mCustomerPrifiles.setNativePlace(this.mNativePlace);
        this.mCustomerPrifiles.setProfession(this.mProfession);
        this.mCustomerPrifiles.setHeight(this.mHeight);
        this.mCustomerPrifiles.setWeight(this.mWeight);
        this.mCustomerPrifiles.setSystolicPressure(this.mSystolicPressure);
        this.mCustomerPrifiles.setDiastolicPressure(this.mDiastolicPressure);
        this.mCustomerPrifiles.setPulse(this.mPulse);
        this.mCustomerPrifiles.setSex(this.mSex);
        this.mCustomerPrifiles.setMaritalStatus(this.mMaritalStatus);
        this.mCustomerPrifiles.setMedicalHistory1(this.mMedicalHistory1);
        this.mCustomerPrifiles.setMedicalHistory2(this.mMedicalHistory2);
        this.mCustomerPrifiles.setMedicalHistory3(this.mMedicalHistory3);
        this.mCustomerPrifiles.setMemo(this.mMemo);
        this.mCustomerPrifiles.setRegisterTime(this.mRegisterTime);
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        Log.d("Insert: ", "Inserting ..");
        int i = 0;
        if (this.mbModify) {
            this.mCustomerPrifiles.setID(this.mId);
            if (localMyDatabaseHandler.updateProfiles(this.mCustomerPrifiles) > 0) {
                myPrompt("修改成功！");
                i = 0;
            } else {
                Log.d("Reading: ", "nRet=" + i);
                myPrompt("修改失败！");
                i = -1;
            }
        } else {
            if (localMyDatabaseHandler.addCustomerProfiles(this.mCustomerPrifiles) == -1L) {
                i = -1;
                myPrompt("添加失败！");
            } else {
                myPrompt("添加成功！");
                i = 0;
            }
        }
        return i;
    }

    private boolean delProfileById(int paramInt) {
        MyDatabaseHandler localMyDatabaseHandler = new MyDatabaseHandler(this);
        CustomerProfilesClass localCustomerProfilesClass = localMyDatabaseHandler.getProfileById(paramInt);
        if (localCustomerProfilesClass != null) {
            localMyDatabaseHandler.deletePrifiles(localCustomerProfilesClass);
            Toast.makeText(this, "删除成功!", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(this, "删除失败!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void initWidget() {
        this.ll_back = ((LinearLayout) findViewById(R.id.ll_back));
        this.tv_save = ((TextView) findViewById(R.id.tv_save));

        tv_male = ((TextView) findViewById(R.id.tv_male));
        tv_female = ((TextView) findViewById(R.id.tv_female));
        tv_unmarried = ((TextView) findViewById(R.id.tv_unmarried));
        tv_married = ((TextView) findViewById(R.id.tv_married));
        et_ChangeDate = ((EditText) findViewById(R.id.et_ChangeDate));

        this.mEditName = ((EditText) findViewById(R.id.customer_name_edit));
        Calendar localCalendar = Calendar.getInstance();
        this.year = localCalendar.get(Calendar.YEAR);
        this.month = localCalendar.get(Calendar.MONTH);
        this.day = localCalendar.get(Calendar.DAY_OF_MONTH);
        this.mBirthday = (this.year + "-" + (1 + this.month) + "-" + this.day);
//        this.btnChangeDate = ((Button) findViewById(R.id.btnChangeDate));
//        this.btnChangeDate.setText(this.mBirthday);
        this.mNativeSpinner = ((Spinner) findViewById(R.id.customer_native_place_spinner));
        this.mProfessionSpinner = ((Spinner) findViewById(R.id.customer_profession_spinner));
        this.mRadioSexGroup = ((RadioGroup) findViewById(R.id.radioSex));
        this.mRadioMaritalStatusGroup = ((RadioGroup) findViewById(R.id.radioMaritalStatus));
        this.mEditHeight = ((EditText) findViewById(R.id.customer_height_edit));
        this.mEditWeight = ((EditText) findViewById(R.id.customer_weight_edit));
        this.mEditSystolicPressure = ((EditText) findViewById(R.id.customer_systolic_pressure_edit));
        this.mEditDiastolicPressure = ((EditText) findViewById(R.id.customer_diastolic_pressure_edit));
        this.mEditPulse = ((EditText) findViewById(R.id.customer_pulse_edit));
        this.mSpinnerMedicalHistory1 = ((Spinner) findViewById(R.id.customer_medical_history1_spinner));
        this.mSpinnerMedicalHistory2 = ((Spinner) findViewById(R.id.customer_medical_history2_spinner));
        this.mSpinnerMedicalHistory3 = ((Spinner) findViewById(R.id.customer_medical_history3_spinner));
        this.mEditMemo = ((EditText) findViewById(R.id.customer_memo_edit));
        addListenerOnButton();
        if (this.mbModify)
            updateWidget(this.mId);
    }

    private String myGetCurrentTime() {
        Calendar localCalendar = Calendar.getInstance();
        return localCalendar.get(Calendar.YEAR) + "-" + (1 + localCalendar.get(Calendar.MONTH)) + "-" + localCalendar.get(Calendar.DAY_OF_MONTH) + " " + localCalendar.get(Calendar.HOUR_OF_DAY) + ":" + localCalendar.get(Calendar.MINUTE) + ":" + localCalendar.get(Calendar.SECOND);
    }

    private void myPrompt(String paramString) {
        Toast.makeText(this, paramString, Toast.LENGTH_SHORT).show();
    }

    private void updateWidget(int paramInt) {
        CustomerProfilesClass localCustomerProfilesClass = new MyDatabaseHandler(this).getProfileById(paramInt);
        if (localCustomerProfilesClass != null) {
            this.mName = localCustomerProfilesClass.getName();
            this.mEditName.setText(this.mName);
            this.mBirthday = localCustomerProfilesClass.getBirthday();
//            this.btnChangeDate.setText(this.mBirthday);
            this.mNativePlace = localCustomerProfilesClass.getNativePlace();
            this.mProfession = localCustomerProfilesClass.getProfession();
            this.mHeight = localCustomerProfilesClass.getHeight();
            this.mEditHeight.setText(this.mHeight);
            this.mWeight = localCustomerProfilesClass.getWeight();
            this.mEditWeight.setText(this.mWeight);
            this.mSystolicPressure = localCustomerProfilesClass.getSystolicPressure();
            this.mEditSystolicPressure.setText(this.mSystolicPressure);
            this.mDiastolicPressure = localCustomerProfilesClass.getDiastolicPressure();
            this.mEditDiastolicPressure.setText(this.mDiastolicPressure);
            this.mPulse = localCustomerProfilesClass.getPulse();
            this.mEditPulse.setText(this.mPulse);
            this.mSex = localCustomerProfilesClass.getSex();
            if (this.mSex.equals("Male")) {
                ((RadioButton) findViewById(R.id.radioMale)).setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.radioFemale)).setChecked(true);
            }

            this.mMaritalStatus = localCustomerProfilesClass.getMaritalStatus();
            if (!this.mMaritalStatus.equals("married")) {
                ((RadioButton) findViewById(R.id.radioMarried)).setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.radioUnmarried)).setChecked(true);
            }

            this.mMemo = localCustomerProfilesClass.getMemo();
            this.mEditMemo.setText(this.mMemo);
            this.mRegisterTime = localCustomerProfilesClass.getRegisterTime();
            this.mDetectTime = localCustomerProfilesClass.getDetectTime();

            this.mMedicalHistory1 = localCustomerProfilesClass.getMedicalHistory1();
//      arrayOfString3 = getResources().getStringArray(R.array.medical_history_arrays);
            this.mMedicalHistory2 = localCustomerProfilesClass.getMedicalHistory2();
            this.mMedicalHistory3 = localCustomerProfilesClass.getMedicalHistory3();
            String[] arrayOfString1 = getResources().getStringArray(R.array.native_place_arrays);
            String[] arrayOfString2 = getResources().getStringArray(R.array.prfession_arrays);
            String[] arrayOfString3 = getResources().getStringArray(R.array.medical_history_arrays);
            int i = 0;
            int j = 0;
            int k = 0;
            int m = 0;
            for (int n = 0; n < arrayOfString3.length; n++) {
                if (i < arrayOfString1.length) {
                    if (arrayOfString1[i].equals(this.mNativePlace)) {
                        this.mNativeSpinner.setSelection(i);
                    }
                    i++;
                }
                if (j < arrayOfString2.length) {
                    if (arrayOfString2[j].equals(this.mProfession)) {
                        this.mProfessionSpinner.setSelection(j);
                    }
                    j++;
                }

                if (k < arrayOfString3.length) {
                    if (arrayOfString3[k].equals(this.mMedicalHistory1)) {
                        this.mSpinnerMedicalHistory1.setSelection(k);
                    }
                    k++;
                }

                if (m < arrayOfString3.length) {
                    if (arrayOfString3[m].equals(this.mMedicalHistory2)) {
                        this.mSpinnerMedicalHistory2.setSelection(m);
                    }
                    m++;
                }

                if (arrayOfString3[n].equals(this.mMedicalHistory3)) {
                    this.mSpinnerMedicalHistory3.setSelection(n);
                }
            }
        } else {
            Toast.makeText(this, "读取数据失败!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addListenerOnButton() {
//        this.btnChangeDate.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View paramAnonymousView) {
//                CustomerProfilesEditActivity.this.showDialog(DATE_DIALOG_ID);
//            }
//        });
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.customer_profiles);
        initWidget();
        setListenter();
        if (this.mCustomerPrifiles == null)
            this.mCustomerPrifiles = new CustomerProfilesClass();
        Bundle localBundle = getIntent().getExtras();

        if (localBundle.getString("customer_profile_edit_type").equals("Modify")) {
//            getWindow().setFeatureInt(7, R.layout.custom_title_button);
//            localButton1 = (Button) findViewById(R.id.customer_modify);
//            localButton2 = (Button) findViewById(R.id.disconnect_device);
            this.mbModify = true;
            this.mId = Integer.parseInt(localBundle.getString("customer_profile_id"));
            Log.e("zj", "modify id=" + this.mId);

            this.tv_save.setText(R.string.modify);

//            ((Button) findViewById(R.id.customer_delete)).setOnClickListener(new View.OnClickListener() {
//                public void onClick(View paramAnonymousView) {
//                    CustomerProfilesEditActivity.this.DelCustomerPrifile();
//                }
//            });
        } else {
//            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_device_manage);
//            localButton2 = (Button) findViewById(R.id.disconnect_device);
//            localButton1 = (Button) findViewById(R.id.connect_device);
            this.tv_save.setText(R.string.new_save);
            this.mbModify = false;

        }
        this.tv_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                CustomerProfilesEditActivity.this.AddNewProfile();
            }
        });
//        localButton2.setText("返回");
        this.ll_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                CustomerProfilesEditActivity.this.CancelAddNewProfile();
            }
        });
        Log.e("zj", " new add");
    }

    private void setListenter() {
        et_ChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tv_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zj", "tv_male");
                isMale = true;
                tv_male.setTextColor(getResources().getColor(R.color.white));
                tv_male.setBackground(getResources().getDrawable(R.drawable.bg_radio_check));
                tv_female.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_female.setBackground(getResources().getDrawable(R.drawable.bg_radio));
            }
        });
        tv_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("zj", "tv_female");

                isMale = false;
                tv_female.setTextColor(getResources().getColor(R.color.white));
                tv_female.setBackground(getResources().getDrawable(R.drawable.bg_radio2));
                tv_male.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_male.setBackground(getResources().getDrawable(R.drawable.bg_radio_check2));
            }
        });
        tv_unmarried.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUnmarried = true;
                tv_unmarried.setTextColor(getResources().getColor(R.color.white));
                tv_unmarried.setBackground(getResources().getDrawable(R.drawable.bg_radio_check));
                tv_married.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_married.setBackground(getResources().getDrawable(R.drawable.bg_radio));
            }
        });
        tv_married.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUnmarried = false;
                tv_married.setTextColor(getResources().getColor(R.color.white));
                tv_married.setBackground(getResources().getDrawable(R.drawable.bg_radio2));
                tv_unmarried.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_unmarried.setBackground(getResources().getDrawable(R.drawable.bg_radio_check2));
            }
        });

    }

    protected Dialog onCreateDialog(int paramInt) {
        switch (paramInt) {
            default:
                return null;
            case 999:
                return null;
//                return new DatePickerDialog(this, this.datePickerListener, this.year, this.month, this.day);
        }
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        return false;
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 4) {
            Builder localBuilder = new Builder(this);
            localBuilder.setMessage("当前记录未保存，是否要退出?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    CustomerProfilesEditActivity.this.finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramAnonymousDialogInterface.cancel();
                }
            });
            localBuilder.create().show();
            return true;
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            default:
                return false;
            case R.id.new_profiles_add:
                String str = mbModify ? "确定要修改客户档案?" : "确定要新增客户档案?";
                Builder localBuilder2 = new Builder(this);
                localBuilder2.setMessage(str).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        CustomerProfilesEditActivity.this.mRegisterTime = CustomerProfilesEditActivity.this.myGetCurrentTime();
                        if (CustomerProfilesEditActivity.this.addNewCustomerProfiles() == 0)
                            CustomerProfilesEditActivity.this.finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        paramAnonymousDialogInterface.cancel();
                    }
                });
                localBuilder2.create().show();
                return true;
            case R.id.new_profiles_cancel:
                Builder localBuilder1 = new Builder(this);
                localBuilder1.setMessage("确定要退出客户档案的编辑?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        CustomerProfilesEditActivity.this.finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        paramAnonymousDialogInterface.cancel();
                    }
                });
                localBuilder1.create().show();
                return true;
        }
    }
}

/* Location:           D:\android studio\fanbianyi\classes-dex2jar (2).jar
 * Qualified Name:     com.tds.test.CustomerProfilesEditActivity
 * JD-Core Version:    0.6.2
 */