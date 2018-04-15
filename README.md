# aos-source
## In order to tell a full DevOps story do the following:

** Add a defect to Octane: **
- Name: Fix login error message
- Details: The error message should be fixed from “Incorrect user name or password.” To “Invalid user name or password, please try again.”
Assign the defect to Yourself.

** Open an IDE with Octane’s IDE plugin installed in it. **
- Make sure you see the defect you just created in the Octane Plugin, if not – validate that you are connected to the correct Octane’s workspace under settings->ALM Octane Settings
- Right click on the defect and select “Start work”
- Go to _accountservice\src\main\java\com\advantage\accountsoap\model\Account.java_, line 50 and change the error text according to the defect.
- Select commit and push
- Review the pipeline and the running tests
- Make sure the test was fixed
- Change the status of the defect to fixed
- Right click on the defect in the IDE plugin and select “Stop work”
