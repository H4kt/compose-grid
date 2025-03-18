<h1 align="center">Compose Grid</h1>

A simple grid implementation for [Compose](https://developer.android.com/compose) and [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform)

![badge-Android](https://img.shields.io/badge/Platform-Android-brightgreen)
![badge-iOS](https://img.shields.io/badge/Platform-iOS-lightgray)
![badge-JVM](https://img.shields.io/badge/Platform-JVM-orange)

-------

<p align="center">
    <a href="#whats-included">What's included 🚀</a> &bull;
    <a href="#setup">Setup 🛠️</a> &bull;
    <a href="#usage">Usage 🛠️</a>
</p>

-------

## What's included
* Support for horizontal and vertical spacing
* Support for row/column item spanning

> Currently the layout expects you to provide both the row and column size definitions.
> This covered our use-case, however in case your use-case is not covered by a [LazyGrid](https://developer.android.com/develop/ui/compose/lists#lazy-grids)
> feel free to file an issue, and I'll look into it

## Setup
Make sure to add the repository
```kotlin
repositories {
    maven("https://repo.h4kt.dev/releases")
}
```

<details>
    <summary>Android/JVM only</summary>

```kotlin
dependencies {
    implementation("dev.h4kt:compose-grid:0.1.0")
}
```
</details>

<details open>
    <summary>Multiplatform</summary>

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("dev.h4kt:compose-grid:0.1.0")
        }
    }
}
```
</details>

## Usage
### Using size DSL to configure rows/columns
```kotlin
Grid(
    rows = sizes {
        fraction(1)
        fraction(2)
        fixed(32.dp)
    },
    columns = sizes {
        fixed(48.dp)
        fraction(1)
        fraction(2)
    }
) { ... }
```
### Adding vertical/horizontal spacing
```kotlin
Grid(
    rows = sizes {
        fraction(1)
        fraction(2)
        fixed(32.dp)
    },
    columns = sizes {
        fixed(48.dp)
        fraction(1)
        fraction(2)
    },
    verticalSpacing = 8.dp,
    horizontalSpacing = 8.dp
) { ... }
```

### Spanning items across multiple rows or columns
```kotlin
Grid(
    rows = sizes {
        fraction(1)
        fraction(2)
        fixed(32.dp)
    },
    columns = sizes {
        fixed(48.dp)
        fraction(1)
        fraction(2)
    },
    verticalSpacing = 8.dp,
    horizontalSpacing = 8.dp
) {
    Box(Modifier.columnSpan(3).rowSpan(2))
    Box(Modifier.columnSpan(2))
    Box()
}
```
