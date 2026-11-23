const form = document.querySelector('#booking-form');
const message = document.querySelector('#message');
const estimate = document.querySelector('#estimate');
const bookings = document.querySelector('#bookings');
const distance = document.querySelector('#distanceKm');
const cab = document.querySelector('#cabType');

async function request(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) {
    let error;
    try { error = await response.json(); } catch { throw new Error(`Request failed (${response.status}).`); }
    throw new Error(error.message || `Request failed (${response.status}). Check the entered details.`);
  }
  return response.json();
}
function showMessage(text, isError = false) {
  message.textContent = text;
  message.classList.toggle('error', isError);
}
function quoteData() {
  if (!distance.reportValidity()) return null;
  return { distanceKm: Number(distance.value), cabType: cab.value };
}
function post(body) {
  return { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) };
}
function money(value) { return `₹${Number(value).toFixed(2)}`; }
function clearEstimate() { estimate.replaceChildren('No estimate yet ', Object.assign(document.createElement('span'), {textContent: '—'})); }
distance.addEventListener('input', clearEstimate);
cab.addEventListener('change', clearEstimate);
document.querySelector('#quote').addEventListener('click', async () => {
  const data = quoteData();
  if (!data) return;
  showMessage('');
  try {
    const result = await request('/api/fare-quote', post(data));
    estimate.replaceChildren('Estimated total ', Object.assign(document.createElement('span'), {textContent: money(result.totalFare)}));
  } catch (error) { showMessage(error.message, true); }
});
form.addEventListener('submit', async event => {
  event.preventDefault();
  const data = quoteData();
  if (!data) return;
  const button = document.querySelector('#submit');
  button.disabled = true;
  showMessage('');
  try {
    const result = await request('/api/bookings', post({...data, customerName: document.querySelector('#customerName').value, pickup: document.querySelector('#pickup').value, destination: document.querySelector('#destination').value}));
    form.reset();
    clearEstimate();
    showMessage(`Booking #${result.id} confirmed · ${money(result.totalFare)}.`);
    await loadBookings();
  } catch (error) { showMessage(error.message, true); }
  finally { button.disabled = false; }
});
async function loadBookings() {
  try {
    const rows = await request('/api/bookings');
    bookings.replaceChildren();
    if (!rows.length) { bookings.textContent = 'No bookings yet. Your journeys will appear here.'; return; }
    rows.forEach(row => {
      const item = document.createElement('div');
      item.className = 'booking-row';
      const customer = document.createElement('div');
      const name = document.createElement('strong');
      name.textContent = row.customerName;
      const id = document.createElement('small');
      id.textContent = `Booking #${row.id}`;
      customer.append(name, id);
      const route = document.createElement('div');
      route.textContent = `${row.pickup} → ${row.destination}`;
      const category = document.createElement('div');
      category.textContent = `${row.cabType} · ${row.distanceKm} km`;
      const fare = document.createElement('div');
      fare.className = 'fare';
      fare.textContent = money(row.totalFare);
      item.append(customer, route, category, fare);
      bookings.append(item);
    });
  } catch (error) { bookings.textContent = `Unable to load bookings: ${error.message}`; }
}
document.querySelector('#refresh').addEventListener('click', loadBookings);
loadBookings();
