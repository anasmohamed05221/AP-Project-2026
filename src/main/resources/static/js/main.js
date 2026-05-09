document.addEventListener('DOMContentLoaded', function () {

  setMinDates();
  initSearchValidation();
  initResultsPageHeader();
  initPriceSlider();
  initWishlistButtons();
  initSortRedirect();
  initSliderSubmit();

});

function setMinDates() {
  var today = new Date().toISOString().split('T')[0]; // format: "2024-10-24"

  var checkIn  = document.getElementById('check_in');
  var checkOut = document.getElementById('check_out');

  if (checkIn)  checkIn.setAttribute('min', today);
  if (checkOut) checkOut.setAttribute('min', today);

  // When check-in changes, update check-out minimum to be at least that date
  if (checkIn && checkOut) {
    checkIn.addEventListener('change', function () {
      checkOut.setAttribute('min', this.value);

      // If check-out is now before check-in, clear it
      if (checkOut.value && checkOut.value < this.value) {
        checkOut.value = '';
      }
    });
  }
}

function initSearchValidation() {
  var form = document.getElementById('searchForm');
  if (!form) return;

  form.addEventListener('submit', function (e) {
    var checkIn  = document.getElementById('check_in');
    var checkOut = document.getElementById('check_out');
    var errorEl  = document.getElementById('dateError');

    if (errorEl) errorEl.classList.remove('visible');

    var cityInput = document.getElementById('city');
    if (cityInput && cityInput.value.trim() === '') {
      e.preventDefault();
      cityInput.focus();
      var cityTooltip = document.querySelector('.city-tooltip');
      cityTooltip.classList.add('visible');
      setTimeout(function () {
        cityTooltip.classList.remove('visible');
      }, 2000);
      return;
    }

    if (checkIn && checkOut && checkIn.value && checkOut.value) {
      var inDate  = new Date(checkIn.value);
      var outDate = new Date(checkOut.value);

      if (outDate <= inDate) {
        e.preventDefault();

        if (errorEl) errorEl.classList.add('visible');

        setTimeout(function () { errorEl.classList.remove('visible'); }, 2000);

        return;
      }
    }
  });
}

function initResultsPageHeader() {
  var titleEl    = document.getElementById('resultsTitle');
  var subtitleEl = document.getElementById('resultsSubtitle');
  if (!titleEl) return;

  var params   = new URLSearchParams(window.location.search);
  var city     = params.get('city')      || '';
  var checkIn  = params.get('check_in')  || '';
  var checkOut = params.get('check_out') || '';
  var guests   = params.get('guests')    || '';

  titleEl.textContent = city ? 'Hotels in ' + city : 'All Available Hotels';

  var nights = null;
  if (checkIn && checkOut) {
    var inDate  = new Date(checkIn  + 'T00:00:00');
    var outDate = new Date(checkOut + 'T00:00:00');
    var diff    = Math.round((outDate - inDate) / (1000 * 60 * 60 * 24));
    if (diff > 0) nights = diff;
  }

  if (nights !== null) {
    var nightLabel = 'Price for ' + nights + ' night' + (nights > 1 ? 's' : '');
    ['nightLabel1', 'nightLabel2', 'nightLabel3'].forEach(function (id) {
      var el = document.getElementById(id);
      if (el) el.textContent = nightLabel;
    });
  }

  var subtitleParts = [];

  if (checkIn && checkOut) {
    subtitleParts.push('Selected dates: ' + formatDate(checkIn) + ' - ' + formatDate(checkOut));
  }

  if (nights !== null) {
    subtitleParts.push(nights + ' night' + (nights > 1 ? 's' : ''));
  }

  if (guests) {
    subtitleParts.push(guests + ' Guest' + (parseInt(guests, 10) > 1 ? 's' : ''));
  }

  if (subtitleEl && subtitleParts.length > 0) {
    subtitleEl.textContent = subtitleParts.join(' · ');
  }
}

function formatDate(isoString) {
  var date   = new Date(isoString + 'T00:00:00');
  var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
  return months[date.getMonth()] + ' ' + date.getDate();
}

function initPriceSlider() {
  var slider   = document.getElementById('priceRange');
  var maxLabel = document.getElementById('priceMax');
  if (!slider || !maxLabel) return;

  slider.addEventListener('input', function () {
    maxLabel.textContent = '$' + this.value;
  });
}

function initWishlistButtons() {
  var buttons = document.querySelectorAll('.wishlist-btn');

  buttons.forEach(function (btn) {
    btn.addEventListener('click', function (e) {
      e.preventDefault();
      e.stopPropagation();

      var icon = this.querySelector('i');
      if (!icon) return;

      if (icon.classList.contains('fa-regular')) {
        icon.classList.remove('fa-regular');
        icon.classList.add('fa-solid');
        this.style.color = '#DC2626';
        this.title = 'Remove from wishlist';
      } else {
        icon.classList.remove('fa-solid');
        icon.classList.add('fa-regular');
        this.style.color = '';
        this.title = 'Save to wishlist';
      }
    });
  });
}

function initSliderSubmit() {
  var slider = document.getElementById('priceRange');
  var form   = document.getElementById('filtersForm');
  if (!slider || !form) return;

  slider.addEventListener('change', function () {
    form.submit();
  });
}

function initSortRedirect() {
  var sortSelect = document.getElementById('sortBy');
  if (!sortSelect) return;

  var params      = new URLSearchParams(window.location.search);
  var currentSort = params.get('sort') || '';
  if (currentSort) {
    sortSelect.value = currentSort;
  }

  sortSelect.addEventListener('change', function () {
    params.set('sort', this.value);
    window.location.search = params.toString();
  });
}
