pipeline {
    agent any

    // 1. Tham số Pipeline (Pipeline Parameters)
    parameters {
        booleanParam(
            name: 'IS_TEST_MODE',
            defaultValue: false,
            description: 'Tích chọn để biên dịch (build) ứng dụng cho môi trường Kiểm thử (Test Environment).'
        )
    }

    // 2. Biến môi trường toàn cục (Global Environment Variables)
    environment {
        KEYSTORE_FILE = "C:/Users/Dell/Documents/my_key.jks"
    }

    // 3. Các giai đoạn thực thi (Execution Stages)
    stages {
        // NÂNG CẤP 1: Tự động đổi tên hiển thị của lượt Build
        stage('Khởi tạo Pipeline') {
            steps {
                script {
                    def buildMode = params.IS_TEST_MODE ? "TEST" : "RELEASE"
                    currentBuild.displayName = "#${BUILD_NUMBER} [${buildMode}]"
                }
                echo "Đã khởi tạo Pipeline. Chế độ Build: ${params.IS_TEST_MODE ? 'TEST' : 'RELEASE'}"
            }
        }

        stage('Kéo mã nguồn (Checkout)') {
            steps {
                echo '--------------------------------------------------'
                echo 'Đang kéo mã nguồn mới nhất từ kho lưu trữ GitHub...'
                echo '--------------------------------------------------'
                checkout scm
            }
        }

        stage('Dọn dẹp dự án (Clean)') {
            steps {
                echo '--------------------------------------------------'
                echo 'Đang dọn dẹp không gian làm việc và các tệp build cũ...'
                echo '--------------------------------------------------'
                bat 'gradlew.bat clean'
            }
        }

        // NÂNG CẤP 2: Chạy Android Lint để quét lỗi tĩnh trước khi build
        stage('Kiểm tra chất lượng code (Lint)') {
            steps {
                echo '--------------------------------------------------'
                echo 'Đang phân tích chất lượng mã nguồn và tài nguyên bằng Android Lint...'
                echo '--------------------------------------------------'
                // Lệnh này sẽ quét lỗi giao diện, bảo mật, hiệu năng. Nếu có lỗi nghiêm trọng (Fatal), nó sẽ tự động dừng build.
                bat 'gradlew.bat lintRelease'
            }
            post {
                            always {
                                publishHTML([
                                    allowMissing: false,
                                    alwaysLinkToLastBuild: true,
                                    keepAll: true,
                                    reportDir: 'app/build/reports',
                                    reportFiles: 'lint-results-release.html',
                                    reportName: 'Báo cáo chất lượng code (Android Lint)'
                                ])
                            }
                        }
        }

        stage('Kiểm thử tự động (Unit Tests)') {
            steps {
                echo '--------------------------------------------------'
                echo 'Đang thực thi các kịch bản kiểm thử tự động (Automated Unit Tests)...'
                echo '--------------------------------------------------'
                bat 'gradlew.bat test'
            }
        }

        stage('Biên dịch & Ký số APK') {
            steps {
                echo '--------------------------------------------------'
                echo "Đang tiến hành biên dịch và đóng gói tệp APK. IS_TEST_MODE = ${params.IS_TEST_MODE}"
                echo '--------------------------------------------------'
                // Trình build.gradle của bạn đã tự động lấy System.getenv("BUILD_NUMBER") nên không cần truyền thêm ở đây nữa
                bat "gradlew.bat assembleRelease -PIS_TEST_MODE=${params.IS_TEST_MODE}"
            }
        }

        stage('Lưu trữ thành phẩm (Archive)') {
            steps {
                echo '--------------------------------------------------'
                echo 'Build thành công! Đang lưu trữ (Archive) các tệp APK thành phẩm...'
                echo '--------------------------------------------------'
                archiveArtifacts artifacts: '**/build/outputs/apk/release/*.apk', fingerprint: true
            }
        }
    }

    // 4. Xử lý sau khi Build (Post-build Actions)
    post {
        success {
            echo '=================================================='
            echo " THÀNH CÔNG: Lượt Build #${BUILD_NUMBER} đã hoàn tất xuất sắc!"
            echo '=================================================='
        }
        failure {
            echo '=================================================='
            echo " THẤT BẠI: Lượt Build #${BUILD_NUMBER} đã gặp lỗi. Vui lòng kiểm tra lại nhật ký (Console Output)!"
            echo '=================================================='
        }
    }
}