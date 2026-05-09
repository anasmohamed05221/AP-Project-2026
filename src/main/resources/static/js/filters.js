const form     = document.getElementById('filtersForm');
const slider   = document.getElementById('priceRange');
const priceMax = document.getElementById('priceMax');

slider.addEventListener('input', () => {
  priceMax.textContent = '$' + slider.value;
});

slider.addEventListener('change', () => form.submit());

form.querySelectorAll('input[type="checkbox"]').forEach(cb => {
  cb.addEventListener('change', () => form.submit());
});

// Custom sort dropdown
const dropdown = document.getElementById('sortDropdown');
const trigger  = dropdown.querySelector('.custom-select-trigger');
const options  = dropdown.querySelectorAll('.custom-option');

trigger.addEventListener('click', () => dropdown.classList.toggle('open'));

options.forEach(opt => {
  opt.addEventListener('click', () => {
    options.forEach(o => o.classList.remove('selected'));
    opt.classList.add('selected');
    trigger.childNodes[0].textContent = opt.textContent;
    form.querySelector('input[name="sort"]').value = opt.dataset.value;
    dropdown.classList.remove('open');
    form.submit();
  });
});

document.addEventListener('click', e => {
  if (!dropdown.contains(e.target)) dropdown.classList.remove('open');
});
