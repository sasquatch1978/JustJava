package com.example.android.justjava;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    boolean isOpt1Checked, isOpt2Checked;

    TextView total_cups, subtotal, tax, toastText;
    CheckBox option1, option2;
    EditText name;
    View layout;

    static String TOTAL_CUPS = "total_cups";
    static String SUBTOTAL = "subtotal";
    static String TAX = "tax";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hides the keyboard when app opens until it is needed.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Identify the views.
        total_cups = findViewById(R.id.quantityText);
        subtotal = findViewById(R.id.priceText);
        tax = findViewById(R.id.taxText);
        name = findViewById(R.id.name);

        // Set the subtotal and tax fields with the proper currency, and set the initial price as $0 instead of $0.00.
        zero();

        // Create your checkboxes/buttons and set their onClickListener to "this".
        option1 = findViewById(R.id.whippedCream);
        option1.setOnClickListener(this);
        option2 = findViewById(R.id.chocolate);
        option2.setOnClickListener(this);

        Button decrease = findViewById(R.id.decrease);
        decrease.setOnClickListener(this);
        Button increase = findViewById(R.id.increase);
        increase.setOnClickListener(this);
        Button submit_order = findViewById(R.id.order);
        submit_order.setOnClickListener(this);

        // Custom Toast
        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        toastText = layout.findViewById(R.id.toastText);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle.
        savedInstanceState.putInt(TOTAL_CUPS, quantity);
        savedInstanceState.putDouble(SUBTOTAL, subPrice);
        savedInstanceState.putDouble(TAX, addTax);
        // Always call the superclass so it can save the view hierarchy state.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy.
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance.
        quantity = savedInstanceState.getInt(TOTAL_CUPS);
        subPrice = savedInstanceState.getDouble(SUBTOTAL);
        addTax = savedInstanceState.getDouble(TAX);
        // Display the saved values.
        total_cups.setText(String.valueOf(quantity));
        subtotal.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(subPrice)));
        tax.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(addTax)));
    }

    public void onClick(View v) {
        // Perform action on click.
        switch (v.getId()) {
            case R.id.whippedCream:
            case R.id.chocolate:
                // Updates price if toppings are selected or not after quantity.
                // Set the price as as $0 instead of $0.00 if quantity is zero.
                if (quantity < 1) zero();
                // Set the price normally, example $2.50.
                else price();
                break;

            case R.id.decrease:
                // Don't allow quantity to go below zero.
                if (quantity == 0) {
                    // Display message. //
                    toastText.setText(R.string.min);
                    Toast toast = new Toast(this);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 420);
                    toast.setView(layout);
                    toast.show();
                    // Exit method early.
                    return;
                }

                // Decrease quantity
                quantity -= 1;
                total_cups.setText(String.valueOf(quantity));
                // Update price if quantity decreases.
                // Set the price as as $0 instead of $0.00 if quantity drops to zero.
                if (quantity < 1) zero();
                // Set the price normally, example $2.50.
                else price();
                break;

            case R.id.increase:
                // Don't allow quantity to go above 100.
                if (quantity == 100) {
                    // Display message.
                    toastText.setText(R.string.max);
                    Toast toast = new Toast(this);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 420);
                    toast.setView(layout);
                    toast.show();
                    // Exit method early.
                    return;
                }

                // Increase quantity
                quantity += 1;
                total_cups.setText(String.valueOf(quantity));
                // Update price if quantity increases.
                price();
                break;

            case R.id.order:
                // Submit Order
                String customer = name.getText().toString();

                // Don't allow an order of zero or without name entered.
                if (quantity == 0 || customer.equals("")) {
                    // Display the toast with the appropriate attributes.
                    toastText.setText(infoToast());
                    Toast toast = new Toast(this);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 420);
                    toast.setView(layout);
                    toast.show();
                    // Exit method early.
                    return;
                }

                // Calculate the price
                double totalPrice = subPrice + addTax;
                String message = createOrderSummary(totalPrice, customer);

                // Compose the order summary email.
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

    /*
      Set the subtotal and tax fields with the proper currency, and set the price as $0 instead of $0.00.
      TODO: Figure out a better way, this doesn't work with localization, if changed to "\\,00" works for euros, but need to get it to work no matter what the currency is.
     */
    public void zero() {
        String dPrice = (NumberFormat.getCurrencyInstance().format(0));
        String curPrice = dPrice.replaceAll("\\.00", "");
        subtotal.setText(String.valueOf(curPrice));
        tax.setText(String.valueOf(curPrice));
    }

    // Calculate subtotal and tax, based on quantity and if toppings are selected.
    public void price() {
        isOpt1Checked = option1.isChecked();
        isOpt2Checked = option2.isChecked();

        // Price for 1 cup of coffee.
        double basePrice = 2.50;

        // If whipped cream is checked.
        if (isOpt1Checked) basePrice += .75;

        // If chocolate is checked.
        if (isOpt2Checked) basePrice += 1.25;

        // Calculate price
        subPrice = quantity * basePrice;
        addTax = subPrice * .0725;

        // Update their TextViews.
        subtotal.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(subPrice)));
        tax.setText(String.valueOf(NumberFormat.getCurrencyInstance().format(addTax)));
    }

    // Build a String so that the first line can be a different color and bold for a toast.
    private SpannableString infoToast() {
        String orderToast = getString(R.string.info);
        orderToast += "\n" + getString(R.string.minName);
        SpannableString infoToast = new SpannableString(orderToast);
        // Set the color.
        infoToast.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.toastTitle)), 0, getString(R.string.info).length(), 0);
        // Set bold.
        infoToast.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, getString(R.string.info).length(), 0);
        return infoToast;
    }

    /**
     * Create summary of the order.
     *
     * @param customer   name
     * @param totalPrice of the order
     * @return text summary
     */
    private String createOrderSummary(double totalPrice, String customer) {
        String cream;
        if (isOpt1Checked) cream = getString(R.string.yes);
        else cream = getString(R.string.no);

        String chocolate;
        if (isOpt2Checked) chocolate = getString(R.string.yes);
        else chocolate = getString(R.string.no);

        String orderMessage = getString(R.string.addWhip, cream);
        orderMessage += "\n" + getString(R.string.addChoc, chocolate);
        orderMessage += "\n" + getString(R.string.orderQuant, quantity);
        orderMessage += "\n" + getString(R.string.orderTot, NumberFormat.getCurrencyInstance().format(totalPrice));
        orderMessage += "\n" + getString(R.string.thankYou, customer);
        return orderMessage;
    }
}
