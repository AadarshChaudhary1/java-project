const conversionOptions = {
  LENGTH: [
    ["METERS_TO_FEET", "Meters → feet"], ["FEET_TO_METERS", "Feet → meters"],
    ["KILOMETERS_TO_MILES", "Kilometers → miles"], ["MILES_TO_KILOMETERS", "Miles → kilometers"]
  ],
  WEIGHT: [
    ["KILOGRAMS_TO_POUNDS", "Kilograms → pounds"], ["POUNDS_TO_KILOGRAMS", "Pounds → kilograms"],
    ["GRAMS_TO_OUNCES", "Grams → ounces"], ["OUNCES_TO_GRAMS", "Ounces → grams"]
  ],
  TEMPERATURE: [
    ["CELSIUS_TO_FAHRENHEIT", "Celsius → Fahrenheit"], ["FAHRENHEIT_TO_CELSIUS", "Fahrenheit → Celsius"],
    ["CELSIUS_TO_KELVIN", "Celsius → Kelvin"], ["KELVIN_TO_CELSIUS", "Kelvin → Celsius"]
  ],
  VOLUME: [
    ["LITERS_TO_GALLONS", "Liters → gallons"], ["GALLONS_TO_LITERS", "Gallons → liters"],
    ["MILLILITERS_TO_FLUID_OUNCES", "Milliliters → fluid ounces"], ["FLUID_OUNCES_TO_MILLILITERS", "Fluid ounces → milliliters"]
  ],
  AREA: [
    ["SQUARE_METERS_TO_SQUARE_FEET", "Square meters → square feet"], ["SQUARE_FEET_TO_SQUARE_METERS", "Square feet → square meters"],
    ["HECTARES_TO_ACRES", "Hectares → acres"], ["ACRES_TO_HECTARES", "Acres → hectares"]
  ]
};

const unaryOperations = new Set(["SQRT", "FACTORIAL", "SIN", "COS", "TAN", "LOG10", "LN", "ABS"]);
const conversionCategory = document.querySelector("#conversion-category");
const conversionChoice = document.querySelector("#conversion-choice");
let activeGameId = null;

function showResult(output, message, isError = false) {
  output.textContent = message;
  output.classList.toggle("error", isError);
}

async function postJson(path, payload) {
  const response = await fetch(path, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  const body = await response.json();
  if (!response.ok) {
    throw new Error(body.error || "That request could not be completed.");
  }
  return body;
}

function numberFrom(form, name) {
  const raw = form.elements.namedItem(name)?.value;
  return raw === "" || raw === undefined ? undefined : Number(raw);
}

function setBusy(form, busy) {
  const button = form.querySelector('button[type="submit"]');
  if (button) button.disabled = busy;
}

document.querySelector("#bmi-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const output = form.querySelector("output");
  setBusy(form, true);
  try {
    const data = await postJson("/api/bmi", {
      weightKg: numberFrom(form, "weightKg"),
      heightMeters: numberFrom(form, "heightMeters")
    });
    showResult(output, `BMI: ${data.bmi.toFixed(1)} · ${data.category}`);
  } catch (error) {
    showResult(output, error.message, true);
  } finally {
    setBusy(form, false);
  }
});

const mathForm = document.querySelector("#math-form");
const mathOperation = mathForm.elements.namedItem("operation");
const secondValueLabel = document.querySelector("#second-value-label");

function updateMathFields() {
  const needsSecondValue = !unaryOperations.has(mathOperation.value);
  secondValueLabel.classList.toggle("hidden", !needsSecondValue);
  mathForm.elements.namedItem("secondValue").required = needsSecondValue;
}

mathOperation.addEventListener("change", updateMathFields);
updateMathFields();

mathForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const output = mathForm.querySelector("output");
  setBusy(mathForm, true);
  try {
    const data = await postJson("/api/math", {
      operation: mathOperation.value,
      firstValue: numberFrom(mathForm, "firstValue"),
      secondValue: numberFrom(mathForm, "secondValue")
    });
    showResult(output, `Result: ${data.result}`);
  } catch (error) {
    showResult(output, error.message, true);
  } finally {
    setBusy(mathForm, false);
  }
});

function updateConversionOptions() {
  conversionChoice.replaceChildren(...conversionOptions[conversionCategory.value].map(([value, label]) => {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = label;
    return option;
  }));
}

conversionCategory.addEventListener("change", updateConversionOptions);
updateConversionOptions();

document.querySelector("#conversion-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const output = form.querySelector("output");
  setBusy(form, true);
  try {
    const data = await postJson("/api/conversions", {
      category: form.elements.namedItem("category").value,
      conversion: form.elements.namedItem("conversion").value,
      value: numberFrom(form, "value")
    });
    showResult(output, `Converted value: ${data.result}`);
  } catch (error) {
    showResult(output, error.message, true);
  } finally {
    setBusy(form, false);
  }
});

function updateGeometryFields(form, mode) {
  const shape = form.elements.namedItem("shape").value;
  const areaTwoDimensions = mode === "area" && ["RECTANGLE", "TRIANGLE"].includes(shape);
  const volumeTwoDimensions = mode === "volume" && ["CYLINDER", "CONE"].includes(shape);
  const volumeThreeDimensions = mode === "volume" && shape === "CUBOID";
  const secondVisible = areaTwoDimensions || volumeTwoDimensions || volumeThreeDimensions;
  const second = form.elements.namedItem("secondDimension");
  const thirdLabel = form.querySelector(".dimension-three-label");
  const third = form.elements.namedItem("thirdDimension");

  form.querySelector(".dimension-one-label").firstChild.textContent =
    mode === "area" && shape === "RECTANGLE" ? "Length"
      : mode === "area" && shape === "TRIANGLE" ? "Base"
        : mode === "area" && shape === "SQUARE" ? "Side length"
          : mode === "volume" && ["CUBE", "CUBOID"].includes(shape) ? "Length / side"
            : "Radius";
  form.querySelector(".dimension-two-label").classList.toggle("hidden", !secondVisible);
  second.required = secondVisible;
  if (areaTwoDimensions) {
    form.querySelector(".dimension-two-label").firstChild.textContent = shape === "TRIANGLE" ? "Height" : "Width";
  } else {
    form.querySelector(".dimension-two-label").firstChild.textContent = shape === "CUBOID" ? "Width" : "Height";
  }
  thirdLabel.classList.toggle("hidden", !volumeThreeDimensions);
  third.required = volumeThreeDimensions;
}

for (const [mode, form] of [["area", document.querySelector("#area-form")], ["volume", document.querySelector("#volume-form")]]) {
  form.elements.namedItem("shape").addEventListener("change", () => updateGeometryFields(form, mode));
  updateGeometryFields(form, mode);
  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const output = form.querySelector("output");
    setBusy(form, true);
    try {
      const body = {
        shape: form.elements.namedItem("shape").value,
        firstDimension: numberFrom(form, "firstDimension"),
        secondDimension: numberFrom(form, "secondDimension"),
        thirdDimension: numberFrom(form, "thirdDimension")
      };
      const data = await postJson(`/api/geometry/${mode}`, body);
      showResult(output, `${mode === "area" ? "Area" : "Volume"}: ${data.result}`);
    } catch (error) {
      showResult(output, error.message, true);
    } finally {
      setBusy(form, false);
    }
  });
}

const startGameButton = document.querySelector("#start-game");
const guessForm = document.querySelector("#guess-form");
const guessInput = guessForm.elements.namedItem("guess");
const guessButton = guessForm.querySelector("button");
const gameResult = document.querySelector("#game-result");

startGameButton.addEventListener("click", async () => {
  startGameButton.disabled = true;
  try {
    const game = await postJson("/api/games/guess/start", {});
    activeGameId = game.gameId;
    guessInput.disabled = false;
    guessButton.disabled = false;
    guessInput.focus();
    showResult(gameResult, game.message);
  } catch (error) {
    showResult(gameResult, error.message, true);
  } finally {
    startGameButton.disabled = false;
  }
});

guessForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!activeGameId) return;
  guessButton.disabled = true;
  try {
    const data = await postJson(`/api/games/guess/${encodeURIComponent(activeGameId)}`, {
      guess: numberFrom(guessForm, "guess")
    });
    showResult(gameResult, `${data.message} Guesses: ${data.attempts}.`);
    if (data.correct) {
      activeGameId = null;
      guessInput.disabled = true;
      guessInput.value = "";
      guessButton.disabled = true;
    }
  } catch (error) {
    showResult(gameResult, error.message, true);
    if (error.message.includes("Start a new game")) {
      activeGameId = null;
      guessInput.disabled = true;
    }
  } finally {
    guessButton.disabled = !activeGameId;
  }
});
