/*
    Make this faster
*/
public class AccountActivity extends BaseMCentActionBarActivity {

    private AccountHelper accountHelper;
    ...

    protected void onCreate(Bundle savedInstanceState) {
        super.onActivityCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        accountHelper = mApplication.getAccountHelper();
        accountHelper.setUp(this);
        refreshActionBar();
    }

    protected void onResume(){

        /*
        Activities contain a title, description and a contact phone number.
        We must use the contact phone number to map to someone from a
        member's address book so that we can display their name on the UI.
        */
        List<Activity> accountActivities = accountHelper.getCachedAccountActivities();
        ContactDataSource contactDataSource = mApplication.getContactDataSource();
        Map<String, Contact> contactMap = contactDataSource.getAddressBookContacts()

        for(Activity activity: accountActivities){
            String phoneNumber = activity.getContactPhoneNumber();

            Contact contact = contactMap.get(phoneNumber);

            if( contact == null){
                continue;
            }

            // Displays the activity on the UI
            showActivity(contact, activity);
        }
    }

    ...

}

