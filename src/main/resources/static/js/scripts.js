
function customReset() {
    document.getElementById("title").value = "";
    document.getElementById("author").value = "";
    document.getElementById("publisher").value = "";
    document.getElementById("genre").value = "";
    document.getElementById("publicationDate").value = "";
    document.getElementById("numberOfPages").value = "";
    document.getElementById("stock").value = "";
    document.getElementById("isbn").value = "";
    document.getElementById("boughtPrice").value = "";
    document.getElementById("ourPrice").value = "";
}
$(document).ready(function () {
    $('#adminAllBooks').DataTable({
        lengthMenu: [10, 15, 20, 25, 50, 75, 100],
        stateSave: true
    });
});
var password = document.getElementById("newPassword")
    , confirm_password = document.getElementById("confirmPassword");

function validatePassword() {
    if (password.value != confirm_password.value) {
        confirm_password.setCustomValidity("New Password fields do not match");
    } else {
        confirm_password.setCustomValidity('');
    }
}
password.onchange = validatePassword;
confirm_password.onkeyup = validatePassword;

function fillInAddress()
{
    document.getElementById("fullName").value = "Vilnius Tourist";
    document.getElementById("phoneNumber").value = "37052629660";
    document.getElementById("countryOrRegion").value = "Lithuania";
    document.getElementById("postCode").value = "01124";
    document.getElementById("streetAddress1").value = "Pilies Street 2";
    document.getElementById("city").value = "Vilnius";
}
function fillInCardInformation()
{
    document.getElementById("nameOnCard").value = "Vilnius Tourist";
    document.getElementById("cardNumber").value = "5745654573156958";
    document.getElementById("expiryMonth").value = "03";
    document.getElementById("expiryYear").value = "2022";
    document.getElementById("cvc").value = "789";
}
