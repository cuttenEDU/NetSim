import javax.swing.*;

public class SomeClass {

    // создаем колбек и его метод
    interface Callback{
        void callingBack();
    }

    Callback callback;

    public void registerCallBack(Callback callback){
        this.callback = callback;
    }

    void doSomething(){
        JOptionPane.showMessageDialog(null, "Выполняется работа");

        // вызываем метод обратного вызова
        callback.callingBack();
    }
}