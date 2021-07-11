let capabilities = new Set();

function toggleCapability(elem) {
  let capability = elem.getAttribute("data-name");
  
  console.log("capability: " + capability);
  
  let checked = elem.checked;
  let includes = capabilities.has(capability);
  
  if (checked && !includes) { // Adding
    capabilities.add(capability);
  } else if (!checked && includes) { // Removing
    capabilities.delete(capability);
  }
  
  let capabilitiesElement = document.querySelector("#capabilities");
  
  let arr = Array.from(capabilities);
  arr.sort();
  
  capabilitiesElement.value = arr.toString()
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