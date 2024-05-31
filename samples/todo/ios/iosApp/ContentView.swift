import UIKit
import SwiftUI
import TodoSample

let virtueApp = TodoIosApplication()

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return virtueApp.createViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}
