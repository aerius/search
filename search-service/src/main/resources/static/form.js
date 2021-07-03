function toggleCapability(elem) {
  let bit = 1 << Number(elem.getAttribute("data-bit"));
  
  let checked = elem.checked;
  
  let capabilitiesElement = document.querySelector("#capabilities");
  let capabilities = Number(capabilitiesElement.value);
  
  if (checked) {
    capabilities =  capabilities | bit;
  } else {
    capabilities =  capabilities & ~bit;
  }
  
  capabilitiesElement.value = capabilities;
}

function init() {
  document.querySelector("#capabilities").value = 0;
  
  let change = elem => {
    const e = new Event("change");
    elem.dispatchEvent(e);
  };
  
  document.querySelectorAll("input[type=checkbox]")
    .forEach(elem => change(elem));
}