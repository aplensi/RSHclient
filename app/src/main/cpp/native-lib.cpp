#include <jni.h>
#include <stdio.h>
#include <fstream>
#include <jni.h>
#include <string>
#include <thread>
#include <vector>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <android/log.h>

using namespace std;

int clientSocket;

vector<string> nameItems;
vector<string> urlImageItems;
vector<string> priceItems;
vector<string> countOfVote;

void addItem(int req, string item);
void takeItems(int request);
int toChar(int req);
string hyeta(int request);

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_rsh_MainActivity_connectToServer(JNIEnv* env, jobject /* this */) {
    clientSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (clientSocket == -1) {
        return env->NewStringUTF("Ошибка при создании сокета");
    }

    struct sockaddr_in serverAddress;
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_addr.s_addr = inet_addr("95.79.161.29");
    serverAddress.sin_port = htons(25565); // Укажите порт сервера

    if (connect(clientSocket, (struct sockaddr*)&serverAddress, sizeof(serverAddress)) == -1) {
        return env->NewStringUTF("Ошибка при подключении к серверу");
    }

    char buffer[256];
    recv(clientSocket, buffer, sizeof(buffer), 0);
    takeItems(2);
    takeItems(3);
    takeItems(1);
    takeItems(5);
    //string st = hyeta(0);
    return env->NewStringUTF(buffer);

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_rsh_MainActivity_createRequest(JNIEnv *env, jobject thiz, jstring request) {
    const char *chars = env->GetStringUTFChars(request, nullptr);
    char buffer[256];
    send(clientSocket, chars, sizeof(chars), 0);
    recv(clientSocket, buffer, sizeof(buffer), 0);
    return env->NewStringUTF(buffer);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_rsh_MainActivity_getVout(JNIEnv *env, jobject thiz, jcharArray key, jstring number) {
    jchar *keyR = env->GetCharArrayElements(key, JNI_FALSE);
    jsize length = env->GetArrayLength(key);
    string result(keyR, keyR + length);
    const char *numberR = env->GetStringUTFChars(number, nullptr);
    char buffer[256] = "k";
    for(int i = 1; i < length+1; i++)
    {
        buffer[i] = result[i-1];
    }
    for(int i = 81; i < sizeof(numberR)+81; i++)
    {
        buffer[i] = numberR[i-81];
    }
    send(clientSocket, buffer, sizeof(buffer), 0);
    recv(clientSocket, buffer, sizeof(buffer), 0);
    return env->NewStringUTF(buffer);
}

void takeItems(int request)
{
    const int bSize = 256;
    char buffer[bSize];
    memset(buffer, 0, sizeof(buffer));
    buffer[0] = 48 + request;
    string st = "";
    send(clientSocket, buffer, strlen(buffer) + 1, 0);
    memset(buffer, 0, sizeof(buffer));
    string message;
    ssize_t bytesRead;
    while ((bytesRead = recv(clientSocket, buffer, sizeof(buffer) - 1, 0)) > 0) {
        buffer[bytesRead] = '\0';
        message += buffer;
        if (bytesRead < bSize - 1) {
            break;
        }
        memset(buffer, 0, sizeof(buffer));
        this_thread::sleep_for(chrono::milliseconds(5));
    }
    for(int i = 0; i < message.size(); i++)
    {
        if(message[i] == '|')
        {
            addItem(request, st);
            st = "";
        }
        else
        {
            st += message[i];
        }
    }
}

string hyeta(int request)
{
    const int bSize = 256;
    char buffer[bSize];
    send(clientSocket, buffer, strlen(buffer) + 1, 0);
    memset(buffer, 0, sizeof(buffer));
    string message;

    ssize_t bytesRead;
    while ((bytesRead = recv(clientSocket, buffer, sizeof(buffer) - 1, 0)) > 0) {
        buffer[bytesRead] = '\0';
        message += buffer;
        if (bytesRead < bSize - 1) {
            break;
        }
        memset(buffer, 0, sizeof(buffer));
        this_thread::sleep_for(chrono::milliseconds(50));
    }
    return to_string(message.size());
}

int toChar(int req)
{
    if(req == 1)
    {
        return 49;
    }
    if(req == 2)
    {
        return 50;
    }
    if(req == 3)
    {
        return 51;
    }
    if(req == 5)
    {
        return 53;
    }

}

void addItem(int req, string item)
{
    if(req == 1)
    {
        nameItems.push_back(item);
        return;
    }
    if(req == 2)
    {
        urlImageItems.push_back(item);
        return;
    }
    if(req == 3)
    {
        priceItems.push_back(item);
        return;
    }
    if(req == 5)
    {
        countOfVote.push_back(item);
        return;
    }
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_rsh_MainActivity_getItem(JNIEnv *env, jobject thiz, jstring name, jint index) {
    const char *chars = env->GetStringUTFChars(name, nullptr);
    int intValue = static_cast<int>(index);
    string st(chars);
    if(st == "name")
    {
        return env->NewStringUTF(nameItems[intValue].c_str());
    }
    if(st == "url")
    {
        return env->NewStringUTF(urlImageItems[intValue].c_str());
    }
    if(st == "price")
    {
        return env->NewStringUTF(priceItems[intValue].c_str());
    }
    if(st == "vote")
    {
        return env->NewStringUTF(countOfVote[intValue].c_str());
    }
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_rsh_MainActivity_getVoutC(JNIEnv *env, jobject thiz, jcharArray key) {
    jchar *keyR = env->GetCharArrayElements(key, JNI_FALSE);
    jsize length = env->GetArrayLength(key);
    string result(keyR, keyR + length);
    char buffer[256] = "c";
    for(int i = 1; i < length+1; i++)
    {
        buffer[i] = result[i-1];
    }
    send(clientSocket, buffer, sizeof(buffer), 0);
    recv(clientSocket, buffer, sizeof(buffer), 0);
    return env->NewStringUTF(buffer);
}