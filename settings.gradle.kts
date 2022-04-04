rootProject.name = "Stock Price"
include(
  ":androidApp",
  ":androidApp:app",
  ":androidApp:core",
  ":androidApp:core:ui",
  ":androidApp:core:data",
  ":androidApp:core:domain",
  ":androidApp:features:splash",
  ":androidApp:features:home",
  ":androidApp:features:settings",
  ":androidApp:features:search",
  ":androidApp:features:about",
  ":androidApp:features:login",
  ":desktopApp",
  ":shared"
)