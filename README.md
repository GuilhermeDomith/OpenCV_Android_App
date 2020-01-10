# OpenCV Android App
Aplicativo Android para demonstrar a utilização do biblioteca OpenCV, e com ela fazer a aplicação de filtros e manipulação de imagens.


## **Download do OpenCV**
Baixar a versão 3.4.6 do OpenCV para Android, no link abaixo se encontram todas as versões para diferentes plataformas.

https://opencv.org/releases/

_Link direto para versão que será usada:_

https://tenet.dl.sourceforge.net/project/opencvlibrary/3.4.6/opencv-3.4.6-android-sdk.zip

## **Importar Módulo OpenCV**
Após feito o download e criado um novo projeto, é preciso importar o módulo a partir do Android Studio. Acesse o menu `File > New > Import Module...`

Em Sorce Directory é preciso passar o caminho de onde foi extraido o OpenCV. Mas é preciso passar o caminho até o SDK para Java, que está dentro da pasta do OpenCV. Como no exemplo abaixo:


```
/home/guilherme/OpenCV-android-sdk/sdk/java
```

Após inserir o caminho do módulo as próximas configurações sugeridas pelo Android Studio podem ser mantidas. 

Altere a perspectiva do Android Studio para `Project` para que possa ser acessado a estrutura do projeto. Copie a pasta `libs`, localizada em `OpenCV-android-sdk/sdk/native`, para o diretório `app/src/main` do seu projeto, a pasta deve ser renomeada para `jniLibs`.

Acesse o menu `File > Project Structure > Dependencies`. Clique sobre o seu App e depois clique em `+`, e adicione o OpenCV como uma dependência para o seu projeto. 


## **Configurar Gradle e Manifest**

Normalmente após importar é preciso corrigir as configuração do Gradle do OpenCV, para estar de acordo com as configurações do seu projeto. 

Acesse o Gradle do módulo e altere as as configurações `compileSdkVersion`, `buildToolsVersion`, `minSdkVersion` e `targetSdkVersion` para o mesmo configurado no Gradle do App. E caso o parâmetro `apply plugin` do módulo esteja como valor `com.android.application`, altere para `com.android.library`.

**Gradle do Módulo OpenCV**
```
apply plugin: 'com.android.library'

android {
    compileSdkVersion 29
    buildToolsVersion "29.0.2"

    defaultConfig {
        minSdkVersion 22
        targetSdkVersion 29
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
        }
    }
}
```

Se o `AndroidManifest.xml` do OpenCV trouxer o parâmetro `uses-sdk`, o mesmo deverá ser removido. Esta configuração, atualmente é feita somente no Gradle.


## **Exemplos Utilizados**
Para o minicurso será utilizado a interface criada no link abaixo. A partir dela vamos usar algumas das funções do OpenCV para manipular imagens.

[activity_main.xml](https://github.com/GuilhermeDomith/OpenCV_Android_App/blob/master/app/src/main/res/layout/activity_main.xml)

Os exemplos que serão criados durante o minicurso podem ser encontrados no link abaixo.

[MainActivity.java](https://github.com/GuilhermeDomith/OpenCV_Android_App/blob/master/app/src/main/java/br/com/opencv/app/MainActivity.java)

Será preciso adicionar no ```AndroidManifest.xml``` do App os recursos que serão utilizados. 

```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## Funcionamento

Abaixo está um exemplo de como é o funcionamento da aplicação que será desenvolvida durante o minicurso.

<p align="center">
<img src="readme/app.gif" width="150px">
<p/>