package com.example.android.justjava;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int quantity = 0;
    double subPrice = 0, addTax = 0;

    TextView total_cups, subtotal, tax;

    CheckBox option1, option2;

    EditText name;

    static String TOTAL_CUPS = "total_cups";
    static String SUBTOTAL = "subtotal";
    static String TAX = "tax";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hides the keyboard when app opens until it is needed. //
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Identify the views. //
        total_cups = (TextView) findViewById(R.id.quantity_text_view);
        subtotal = (TextView) findViewById(R.id.price_text_view);
        tax = (TextView) findViewById(R.id.tax_text_view);

        name = (EditText) findViewById(R.id.name);

        // Set the subtotal and tax fields with the proper currency, and set the initial price as $0 instead of $0.00. //
        String dPrice = (NumberFormat.getCurrencyInstance().format(0));
        String curPrice = dPrice.replaceAll("\\.00", "");
        subtotal.setText(String.valueOf(curPrice));
        tax.setText(String.valueOf(curPrice));

        // Create your checkboxes/buttons and set their onClickListener to "this". //
        option1 = (CheckBox) findViewById(R.id.check1);
        option1.setOnClickListener(this);
        option2 = (CheckBox) findViewById(R.id.check2);
        option2.setOnClickListener(this);

        Button decrease = (Button) findViewById(R.id.button1);
        decrease.setOnClickListener(this);
        Button increase = (Button) findViewById(R.id.button2);
        increase.setOnClickListener(this);
        Button submit_order = (Button) findViewById(R.id.button3);
        submit_order.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle. //
        savedInstanceState.putInt(TOTAL_CUPS, quantity);
        savedInstanceState.putDouble(SUBTOTAL, subPrice);
        savedInstanceState.putDouble(TAX, addTax);
        // Always call the superclass so it can save the view hierarchy state. //
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy. //
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance. //
        quantity = savedInstanceState.getInt(TOTAL_CUPS);
        subPrice = savedInstanceState.getDouble(SUBTOTAL);
        addTax = savedInstanceState.getDouble(TAX);
        // Display the saved values. //
        total_cups.setText(String.valueOf(quantity));
        subtotal.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(subPrice)));
        tax.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(addTax)));
    }

    @SuppressLint("StringFormatInvalid")
    public void onClick(View v) {
        boolean isOpt1Checked = option1.isChecked();
        boolean isOpt2Checked = option2.isChecked();

        // Price for 1 cup of coffee. //
        double basePrice = 2.50;

        // If whipped cream is checked. //
        if (isOpt1Checked) basePrice = basePrice + .75;

        // If chocolate is checked. //
        if (isOpt2Checked) basePrice = basePrice + 1.25;

        // Perform action on click //
        switch (v.getId()) {

            case R.id.check1:
                // Updates price if whipped cream is selected or not after quantity. //
                subPrice = quantity * basePrice;
                addTax = subPrice * .0725;
                subtotal.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(subPrice)));
                tax.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(addTax)));
                break;

            case R.id.check2:
                // Updates price if chocolate is selected or not after quantity. //
                subPrice = quantity * basePrice;
                addTax = subPrice * .0725;
                subtotal.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(subPrice)));
                tax.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(addTax)));
                break;

            case R.id.button1:
                // Don't allow to go below 0. //
                if (quantity == 0) {
                    // Display message. //
                    Toast toast = Toast.makeText(this, R.string.min, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 420);
                    toast.show();
                    // Exit method early. //
                    return;
                }

                // Decrease quantity //
                quantity = quantity - 1;
                subPrice = quantity * basePrice;
                addTax = subPrice * .0725;
                total_cups.setText(String.valueOf(quantity));
                subtotal.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(subPrice)));
                tax.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(addTax)));
                break;

            case R.id.button2:
                // Don't allow to go above 100. //
                if (quantity == 100) {
                    // Display message. //
                    Toast toast = Toast.makeText(this, R.string.max, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 420);
                    toast.show();
                    // Exit method early. //
                    return;
                }

                // Increase quantity //
                quantity = quantity + 1;
                subPrice = quantity * basePrice;
                addTax = subPrice * .0725;
                total_cups.setText(String.valueOf(quantity));
                subtotal.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(subPrice)));
                tax.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(addTax)));
                break;

            case R.id.button3:
                // Don't allow an order of 0. //
                if (quantity == 0) {
                    // Display message. //
                    Toast toast = Toast.makeText(this, R.string.min, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 420);
                    toast.show();
                    // Exit method early. //
                    return;
                }

                // Submit Order //
                String customer = name.getText().toString();

                // Calculate the price //
                subPrice = calculatePrice(isOpt1Checked, isOpt2Checked);
                String message = createOrderSummary(subPrice, customer, isOpt1Checked, isOpt2Checked);

                // Compose the order summary email. //
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject, customer));
                intent.putExtra(Intent.EXTRA_TEXT, message);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Calculates the price of the order.
     *
     * @param isOpt1Checked add whipped cream
     * @param isOpt2Checked add chocolate
     * @return total price
     */
    private double calculatePrice(boolean isOpt1Checked, boolean isOpt2Checked) {
        // Price for 1 cup of coffee. //
        double basePrice = 2.50;

        // If whipped cream is checked. //
        if (isOpt1Checked) basePrice = basePrice + .75;

        // If chocolate is checked. //
        if (isOpt2Checked) basePrice = basePrice + 1.25;

        // Calculate the price. //
        subPrice = quantity * basePrice;
        addTax = subPrice * .0725;
        return subPrice + addTax;
    }

    /**
     * Create summary of the order.
     *
     * @param customer      name
     * @param price         of the order
     * @param isOpt1Checked is whether or not to add whipped cream to the coffee
     * @param isOpt2Checked is whether or not to add chocolate to the coffee
     * @return text summary
     */
    @SuppressLint("StringFormatInvalid")
    private String createOrderSummary(double price, String customer, boolean isOpt1Checked, boolean isOpt2Checked) {
        String cream;
        if (isOpt1Checked) cream = getString(R.string.yes);
        else cream = getString(R.string.no);

        String chocolate;
        if (isOpt2Checked) chocolate = getString(R.string.yes);
        else chocolate = getString(R.string.no);

        String orderMessage = getString(R.string.addWhip, cream);
        orderMessage += "\n" + getString(R.string.addChoc, chocolate);
        orderMessage += "\n" + getString(R.string.orderQuant, quantity);
        orderMessage += "\n" + getString(R.string.orderTot, NumberFormat.getCurrencyInstance().format(price));
        orderMessage += "\n" + getString(R.string.thankYou, customer) + getString(R.string.exclamation);
        return orderMessage;
    }
}
