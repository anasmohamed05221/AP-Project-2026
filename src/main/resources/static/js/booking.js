const pricePerNight = parseFloat(document.getElementById("roomData").dataset.price);

const checkInPicker = document.getElementById("checkInPicker");
const checkOutPicker = document.getElementById("checkOutPicker");
const checkInHidden = document.getElementById("checkInHidden");
const checkOutHidden = document.getElementById("checkOutHidden");

const nightsText = document.getElementById("nightsText");
const basePriceEl = document.getElementById("basePrice");
const serviceFeeEl = document.getElementById("serviceFee");
const totalPriceEl = document.getElementById("totalPrice");
const bookingError = document.getElementById("bookingError");

const cleaning = 45;

function calculatePrice() {
  bookingError.textContent = "";

  if (!checkInPicker.value || !checkOutPicker.value) {
    nightsText.textContent = "0";
    basePriceEl.textContent = "0.00";
    serviceFeeEl.textContent = "0.00";
    totalPriceEl.textContent = "0.00";
    return;
  }

  const start = new Date(checkInPicker.value);
  const end = new Date(checkOutPicker.value);
  const nights = (end - start) / (1000 * 60 * 60 * 24);

  if (nights <= 0) {
    bookingError.textContent = "Check-out date must be after check-in date.";
    return;
  }

  const base = nights * pricePerNight;
  const service = base * 0.12;
  const total = base + cleaning + service;

  nightsText.textContent = nights;
  basePriceEl.textContent = base.toFixed(2);
  serviceFeeEl.textContent = service.toFixed(2);
  totalPriceEl.textContent = total.toFixed(2);

  checkInHidden.value = checkInPicker.value;
  checkOutHidden.value = checkOutPicker.value;
}

document.getElementById("bookingForm").addEventListener("submit", function (e) {
  if (!checkInPicker.value || !checkOutPicker.value) {
    e.preventDefault();
    bookingError.textContent = "Please select check-in and check-out dates.";
    return;
  }
  if (new Date(checkOutPicker.value) <= new Date(checkInPicker.value)) {
    e.preventDefault();
    bookingError.textContent = "Check-out date must be after check-in date.";
  }
});

flatpickr("#checkInPicker", { minDate: "today", onChange: calculatePrice });
flatpickr("#checkOutPicker", { minDate: "today", onChange: calculatePrice });